<template>
  <!-- 添加按钮 -->
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 闪 购</el-button
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
      prop="activityName"
      align="center"
      label="闪购活动名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="productName"
      align="center"
      label="商品名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="startTime"
      align="center"
      label="开始时间"
      show-overflow-tooltip
    />
    <el-table-column
      prop="endTime"
      align="center"
      label="结束时间"
      show-overflow-tooltip
    />
    <el-table-column prop="activityStatus" align="center" label="活动状态" />
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
          @click="del(scoped.row.activityId)"
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

  <!-- 编辑对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑闪购活动"
    width="500"
    center
  >
    <el-form
      ref="editForm"
      :model="editQuery"
      label-width="auto"
      style="max-width: 600px"
    >
      <el-form-item label="闪购活动">
        <el-input v-model="editQuery.activityName" disabled />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="editQuery.productName" disabled />
      </el-form-item>
      <el-form-item label="活动状态">
        <el-input v-model="editQuery.activityStatus" disabled />
      </el-form-item>

      <el-form-item label="开始时间">
        <el-date-picker
          v-model="editQuery.startTime"
          type="datetime"
          placeholder="请选择开始时间"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledEditStartDate"
        />
      </el-form-item>

      <el-form-item label="结束时间">
        <el-date-picker
          v-model="editQuery.endTime"
          type="datetime"
          placeholder="请选择结束时间"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledEditEndDate"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitEdit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 添加对话框 -->
  <el-dialog v-model="addDialogVisible" title="新增闪购活动" width="500" center>
    <el-form
      ref="addForm"
      :model="addQuery"
      label-width="auto"
      style="max-width: 600px"
    >
      <el-form-item label="活动名称">
        <el-input
          v-model="addQuery.activityName"
          placeholder="请输入活动名称"
        />
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

      <el-form-item label="开始时间">
        <el-date-picker
          v-model="addQuery.startTime"
          type="datetime"
          placeholder="请选择开始时间"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledAddStartDate"
        />
      </el-form-item>

      <el-form-item label="结束时间">
        <el-date-picker
          v-model="addQuery.endTime"
          type="datetime"
          placeholder="请选择结束时间"
          style="width: 100%"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="disabledAddEndDate"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="commitAdd">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>


<script setup>
import { Plus, Delete, Edit, View } from "@element-plus/icons-vue";
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { doGet, doPost, doDelete, doPut } from "../../http/httpRequest";

const tableData = ref([]);

onMounted(() => {
  loadData(1);
});

/**
 * @description 禁用今天之前的日期(不包括今天)
 */
const disabledAddStartDate = (time) => {
  let today = new Date();
  today.setHours(0, 0, 0, 0);
  return time.getTime() < today.getTime();
};

const disabledEditStartDate = (time) => {
  let today = new Date();
  today.setHours(0, 0, 0, 0);
  return time.getTime() < today.getTime();
};

/**
 * @description 禁用开始时间之前的日期
 */
const disabledAddEndDate = (time) => {
  let startTime = new Date(addQuery.value.startTime);

  return time.getTime() < startTime.getTime();
};

const disabledEditEndDate = (time) => {
  let startTime = new Date(editQuery.value.startTime);

  return time.getTime() < startTime.getTime();
};

/****************************************** 分页 *********************************************/
const num = ref(0);
const size = ref(0);
const total = ref(0);

/**
 * @description 页面跳转
 */
const toPage = (current) => {
  loadData(current);
};

/**
 * @description 加载闪购活动信息
 */
const loadData = (current) => {
  doGet("/activity/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      num.value = res.data.data.current;
      size.value = res.data.data.size;
      total.value = res.data.data.total;

      tableData.value.forEach((item) => {
        switch (item.activityStatus) {
          case 0:
            item.activityStatus = "未开始";
            break;
          case 1:
            item.activityStatus = "已结束";
            break;
          case 2:
            item.activityStatus = "进行中";
            break;
        }
      });
    }
  });
};

/*************************************** 编辑 ******************************************/
const editDialogVisible = ref(false);
const editForm = ref(null);
const editQuery = ref({
  activityId: "",
  activityName: "",
  productId: "",
  activityStatus: 0,
  startTime: "",
  endTime: "",
});

/**
 * @description 活动状态枚举
 */
const ActivityStatus = {
  0: "未开始",
  1: "已结束",
  2: "进行中",
};

/**
 * @description 编辑
 */
const view = (row) => {
  editQuery.value = JSON.parse(JSON.stringify(row));

  editDialogVisible.value = true;
};

/**
 * @description 提交修改
 */
const commitEdit = () => {
  if (editQuery.value.startTime >= editQuery.value.endTime) {
    ElMessage.error("开始时间不能晚于结束时间");
    editQuery.value = {};
    editDialogVisible.value = false;
    return;
  }

  if (new Date(editQuery.value.endTime) < new Date()) {
    ElMessage.error("结束时间不能早于当前时间");
    editQuery.value = {};
    editDialogVisible.value = false;
    return;
  }

  if (
    new Date(editQuery.value.startTime) < new Date() &&
    new Date(editQuery.value.endTime) > new Date()
  ) {
    editQuery.value.activityStatus = 2;
  }

  let activityStatus = editQuery.value.activityStatus;
  if (typeof activityStatus === "string" && !/^\d+$/.test(activityStatus)) {
    editQuery.value.activityStatus = transformStatus(
      editQuery.value.activityStatus,
      ActivityStatus,
      null
    );
  }

  let json = JSON.stringify(editQuery.value);

  doPut("/activity/update", json)
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("修改成功");
        toPage(1);
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
/*************************************** 添加 ******************************************/
const addDialogVisible = ref(false);
const addForm = ref(null);
const productOptions = ref([]);
const addQuery = ref({
  activityName: "",
  productId: "",
  activityStatus: 0,
  startTime: "",
  endTime: "",
});

/**
 * @description 加载所有商品信息
 */
const loadProducts = () => {
  doGet("/product/list", {})
    .then((res) => {
      if (res.data.code === 200) {
        productOptions.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

/**
 * @description 添加
 */
const add = () => {
  // 加载所有商品信息
  loadProducts();

  addDialogVisible.value = true;
};

/**
 * @description 提交添加
 */
const commitAdd = () => {
  // 校验开始时间是否在结束时间之前
  if (addQuery.value.startTime >= addQuery.value.endTime) {
    ElMessage.error("开始时间不能晚于结束时间");
    addQuery.value = {};
    addDialogVisible.value = false;
    return;
  }

  // 现在时间是否在结束时间之后
  if (new Date(addQuery.value.endTime) < new Date()) {
    ElMessage.error("结束时间不能早于当前时间");
    addQuery.value = {};
    addDialogVisible.value = false;
    return;
  }

  // 现在时间是否在结束时间之前
  if (
    new Date(addQuery.value.startTime) < new Date() &&
    new Date(addQuery.value.endTime) > new Date()
  ) {
    addQuery.value.activityStatus = 2;
  }

  let json = JSON.stringify(addQuery.value);

  doPost("/activity/save", json)
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("添加成功");
        toPage(1);
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });

  addDialogVisible.value = false;
};

/*************************************** 删除 ******************************************/
/**
 * @description 删除
 */
const del = (activityId) => {
  doDelete("/activity/delete", {
    activityId: activityId,
  })
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("删除成功");
        toPage(1);
      } else {
        ElMessage.error(res.data.msg);
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