<template>
  <!-- 添加按钮 -->
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 订 单</el-button
  >

  <!-- 空数据图片 -->
  <img
    class="empty_data_img"
    src="../../assets/empty_data.svg"
    alt="空空如也~"
    v-if="tableData.length == 0"
  />

  <!-- 表格 -->
  <el-table :data="tableData" stripe style="width: 100%" v-else>
    <el-table-column
      prop="orderId"
      align="center"
      label="订单编号"
      show-overflow-tooltip
    />
    <el-table-column
      prop="userName"
      align="center"
      label="用户名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="productName"
      align="center"
      label="商品名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="activityName"
      align="center"
      label="闪购活动"
      show-overflow-tooltip
    />
    <el-table-column
      prop="amount"
      align="center"
      label="订单总价"
      show-overflow-tooltip
    />
    <el-table-column
      prop="orderStatus"
      align="center"
      label="订单状态"
      show-overflow-tooltip
    />
    <el-table-column
      prop="payStatus"
      align="center"
      label="支付状态"
      show-overflow-tooltip
    />
    <el-table-column
      prop="shippingStatus"
      align="center"
      label="运输状态"
      show-overflow-tooltip
    />
    <el-table-column
      prop="note"
      align="center"
      label="备注"
      show-overflow-tooltip
    />
    <el-table-column
      prop="createTime"
      align="center"
      label="添加时间"
      show-overflow-tooltip
    />
    <el-table-column
      prop="options"
      align="center"
      fixed="right"
      label="操作"
      width="180"
    >
      <template #default="scoped">
        <el-button
          class="editBtn"
          :icon="Edit"
          size="small"
          @click="view(scoped.row)"
          round
          >编 辑</el-button
        >
        <el-button
          class="delBtn"
          :icon="Delete"
          size="small"
          @click="del(scoped.row.orderId)"
          round
          >删 除</el-button
        >
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页组件 -->
  <el-pagination
    class="mt-4"
    layout="prev, pager, next"
    :page-size="size"
    :total="total"
    @prev-click="toPage"
    @next-click="toPage"
    @current-change="toPage"
  />

  <!-- 添加对话框 -->
  <el-dialog v-model="addDialogVisible" title="新增订单" width="500" center>
    <el-form ref="addForm" label-width="auto" :model="addQuery">
      <el-form-item label="用户名称">
        <el-select v-model="addQuery.userId" placeholder="请选择用户">
          <el-option
            v-for="item in userOptions"
            :key="item.userId"
            :label="item.realName"
            :value="item.userId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="商品名称">
        <el-select v-model="addQuery.productId" placeholder="请选择商品">
          <el-option
            v-for="item in productOptions"
            :key="item.productId"
            :label="item.productName"
            :value="item.productId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="备注">
        <el-input v-model="addQuery.note" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitAdd">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 编辑对话框 -->
  <el-dialog v-model="editDialogVisible" title="编辑订单" width="500" center>
    <el-form ref="editForm" label-width="auto" :model="editQuery">
      <el-form-item label="用户名称">
        <el-input v-model="editQuery.userName" disabled />
      </el-form-item>

      <el-form-item label="商品名称">
        <el-input v-model="editQuery.productName" disabled />
      </el-form-item>

      <el-form-item label="闪购活动">
        <el-input v-model="editQuery.activityName" disabled />
      </el-form-item>

      <el-form-item label="订单总结">
        <el-input v-model="editQuery.amount" disabled />
      </el-form-item>

      <el-form-item label="订单状态">
        <el-select v-model="editQuery.orderStatus" placeholder="请选择订单状态">
          <el-option label="未完成" value="0" />
          <el-option label="已完成" value="1" />
        </el-select>
      </el-form-item>

      <el-form-item label="支付状态">
        <el-select v-model="editQuery.payStatus" placeholder="请选择支付状态">
          <el-option label="未支付" value="0" />
          <el-option label="已支付" value="1" />
        </el-select>
      </el-form-item>

      <el-form-item label="运输状态">
        <el-select
          v-model="editQuery.shippingStatus"
          placeholder="请选择运输状态"
        >
          <el-option label="未发货" value="0" />
          <el-option label="已发货" value="1" />
          <el-option label="已收货" value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="备注">
        <el-input v-model="editQuery.note" />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitEdit">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>


<script setup>
import { Plus, Delete, Edit, View } from "@element-plus/icons-vue";
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { doGet, doPost, doPut, doDelete } from "../../http/httpRequest";

const tableData = ref([]);

onMounted(() => {
  loadData(1);
});

/****************************************** 分页 *********************************************/
const size = ref(0);
const total = ref(0);

/**
 * @description 页面跳转
 */
const toPage = (current) => {
  loadData(current);
};

/**
 * @description 加载预约活动信息
 */
const loadData = (current) => {
  doGet("/order/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      total.value = res.data.data.total;
      size.value = res.data.data.size;

      tableData.value.forEach((item) => {
        item.orderStatus = item.orderStatus === 1 ? "已完成" : "未完成";
        item.payStatus = item.payStatus === 1 ? "已支付" : "未支付";

        switch (item.shippingStatus) {
          case 0:
            item.shippingStatus = "未发货";
            break;
          case 1:
            item.shippingStatus = "已发货";
            break;
          case 2:
            item.shippingStatus = "已收货";
            break;
        }
      });
    }
  });
};

/****************************************** 新增 *********************************************/
const addForm = ref(null);
const addDialogVisible = ref(false);
const userOptions = ref([]);
const productOptions = ref([]);
let addQuery = ref({
  userId: "",
  productId: "",
});

/**
 * @description 加载所有用户信息
 */
const loadUsers = () => {
  doGet("/user/allUserName", {})
    .then((res) => {
      if (res.data.code === 200) {
        userOptions.value = res.data.data;
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

/**
 * @description 加载所有商品信息
 */
const loadProducts = () => {
  doGet("/product/list", {})
    .then((res) => {
      if (res.data.code === 200) {
        productOptions.value = res.data.data;
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

const add = () => {
  // 加载所有用户信息
  loadUsers();

  // 加载所有商品信息
  loadProducts();

  addDialogVisible.value = true;
};

const commitAdd = () => {
  let json = JSON.stringify(addQuery.value);

  doPost("/order/save", json)
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("新增成功");
        loadData(1);
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });

  addDialogVisible.value = false;
};
/****************************************** 编辑 *********************************************/
const editDialogVisible = ref(false);
const editQuery = ref({
  orderId: "",
  userName: "",
  productName: "",
  activityName: "",
  orderStatus: "",
  payStatus: "",
  shippingStatus: "",
  note: "",
});

const OrderStatus = {
  COMPLETED: "已完成",
  UNCOMPLETED: "未完成",
};

const PayStatus = {
  PAID: "已支付",
  UNPAID: "未支付",
};

const ShippingStatus = {
  SHIPPED: "已发货",
  UNSHIPPED: "未发货",
  RECEIVED: "已收货",
};

/**
 * @description 查看订单详情
 */
const view = (row) => {
  editQuery.value = JSON.parse(JSON.stringify(row));

  editDialogVisible.value = true;
};

/**
 * @description 提交修改
 */
const commitEdit = () => {
  let orderStatus = editQuery.value.orderStatus;
  if (typeof orderStatus === "string" && !/^\d+$/.test(orderStatus)) {
    editQuery.value.orderStatus = transformStatus(
      editQuery.value.orderStatus,
      OrderStatus,
      null
    );
  }

  let payStatus = editQuery.value.payStatus;
  if (typeof payStatus === "string" && !/^\d+$/.test(payStatus)) {
    editQuery.value.payStatus = transformStatus(
      editQuery.value.payStatus,
      PayStatus,
      null
    );
  }

  let shippingStatus = editQuery.value.shippingStatus;
  if (typeof shippingStatus === "string" && !/^\d+$/.test(shippingStatus)) {
    editQuery.value.shippingStatus = transformStatus(
      editQuery.value.shippingStatus,
      ShippingStatus,
      null
    );
  }

  let json = JSON.stringify(editQuery.value);

  doPut("/order/update", json)
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("修改成功");
        loadData(1);
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });

  editDialogVisible.value = false;
};

/**
 * @description 枚举转换
 */
const transformStatus = (status, enumObj, defaultValue) => {
  const key = Object.keys(enumObj).find((key) => enumObj[key] === status);
  return key ? (enumObj[key] === status ? 1 : 0) : defaultValue;
};
/****************************************** 删除 *********************************************/
const del = (orderId) => {
  doDelete("/order/delete", {
    orderId: orderId,
  })
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("删除成功");
        toPage(1);
      } else {
        ElMessage.error("删除失败");
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};
</script>


<style scoped>
.el-table {
  margin-top: 20px;
  margin-bottom: 20px;
}

.empty_data_img {
  width: 200px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translateX(-25%) translateY(-50%);
}

.el-pagination {
  position: absolute;
  bottom: 35px;
  left: 50%;
  transform: translateX(-30%);
}

/******************************************** 按钮样式 ****************************************** */
.addBtn {
  border: none;
  color: #fff;
  background-color: var(--flash-blue-lighter-2);
}

.addBtn:hover {
  background-color: var(--flash-blue-lighter-3);
}

.delBtn {
  border: none;
  color: var(--flash-red-lighter-4);
  background-color: var(--flash-red-lighter-2);
}

.delBtn:hover {
  background-color: var(--flash-red-lighter-3);
}

.editBtn {
  border: none;
  color: #fff;
  background-color: var(--flash-green-color);
}

.editBtn:hover {
  background-color: var(--flash-green-lighter-2);
}
</style>