<template>
  <!-- 新增按钮 -->
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 预 约</el-button
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
      prop="reservationName"
      align="center"
      label="预约活动名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="realName"
      align="center"
      label="用户名称"
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
          class="delBtn"
          :icon="Delete"
          size="small"
          @click="del(scoped.row.id)"
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
  <el-dialog v-model="addDialogVisible" title="新增用户" width="500" center>
    <el-form ref="addForm" label-width="auto" :model="addQuery">
      <el-form-item label="预约活动名称">
        <el-select
          v-model="addQuery.reservationId"
          placeholder="请选择预约活动"
        >
          <el-option
            v-for="item in reservationOptions"
            :key="item.reservationId"
            :label="item.reservationName"
            :value="item.reservationId"
          />
        </el-select>
      </el-form-item>

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
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { doGet, doPost, doDelete } from "../../http/httpRequest";

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
  doGet("/reservation_user/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      total.value = res.data.data.total;
      size.value = res.data.data.size;
    } else {
      ElMessage.error(res.data.msg);
    }
  });
};

/****************************************** 新增 *********************************************/
const addDialogVisible = ref(false);
const addForm = ref(null);
let reservationOptions = ref([]);
let userOptions = ref([]);
let addQuery = ref({
  reservationId: "",
  userId: "",
});

/**
 * @description 加载正在进行中的预约活动的信息
 */
const loadReservations = () => {
  doGet("/reservation/ongoingReservations", {})
    .then((res) => {
      if (res.data.code === 200) {
        reservationOptions.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

/**
 * @description 加载所有用户信息
 */
const loadUsers = () => {
  doGet("/user/allUserName", {})
    .then((res) => {
      if (res.data.code === 200) {
        userOptions.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      ElMessage.error(err);
    });
};

/**
 * @description 新增预约信息
 */
const add = () => {
  addQuery.value = {};

  // 加载正在进行中的预约活动的信息
  loadReservations();

  // 加载所有用户信息
  loadUsers();

  addDialogVisible.value = true;
};

/**
 * @description 提交新增预约活动信息
 */
const commitAdd = () => {
  let json = JSON.stringify(addQuery.value);

  doPost("/reservation_user/reserve", json)
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("预约成功");
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

/****************************************** 删除 *********************************************/
/**
 * @description 取消预约
 */
const del = (id) => {
  doDelete("/reservation_user/cancelReserve", {
    id: id,
  })
    .then((res) => {
      if (res.data.code === 200) {
        ElMessage.success("删除成功");
        loadData(1);
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
  background-color: var(--flash-red-lighter-2);
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