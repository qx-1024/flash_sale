<template>
  <el-button class="addBtn" :icon="Plus" @click="add" size="small" round
    >添 加 预 约</el-button
  >
  <img
    class="empty_data_img"
    src="../../assets/empty_data.svg"
    alt="空空如也~"
    v-if="tableData.length == 0"
  />
  <el-table :data="tableData" stripe style="width: 100%" v-else>
    <el-table-column
      prop="reservationName"
      align="center"
      label="预约活动名称"
      show-overflow-tooltip
    />
    <el-table-column
      prop="userName"
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
          class="editBtn"
          :icon="Edit"
          size="small"
          @click="view(scoped.row.id)"
          round
          >编 辑</el-button
        >
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
</template>


<script setup>
import { onMounted, ref } from "vue";
import { Plus, Delete, Edit, View } from "@element-plus/icons-vue";
import { doGet } from "../../http/httpRequest";

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
  doGet("/reservation/page", {
    current: current,
  }).then((res) => {
    if (res.data.code === 200) {
      tableData.value = res.data.data.records;
      total.value = res.data.data.total;
      size.value = res.data.data.size;

      tableData.value.forEach((item) => {
        // 使用 switch
        switch (item.reservationStatus) {
          case 0:
            item.reservationStatus = "未开始";
            break;
          case 1:
            item.reservationStatus = "已结束";
            break;
          case 2:
            item.reservationStatus = "进行中";
            break;
        }
      });
    }
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