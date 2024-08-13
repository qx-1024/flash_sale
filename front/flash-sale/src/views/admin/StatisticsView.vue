<template>
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="6">
          <el-statistic
            value-style="font-size: 28px; color: var(--flash-green-color)"
            title="商 品 总 数"
            :value="productCount"
          />
        </el-col>
        <el-col :span="6">
          <el-statistic
            value-style="font-size: 28px; color: var(--flash-green-color)"
            title="预 约 活 动 总 数"
            :value="reservationCount"
          />
        </el-col>
        <el-col :span="6">
          <el-statistic
            value-style="font-size: 28px; color: var(--flash-green-color)"
            title="闪 购 活 动 总 数"
            :value="activityCount"
          />
        </el-col>
        <el-col :span="6">
          <el-statistic
            value-style="font-size: 28px; color: var(--flash-red-lighter-2); font-weight: bold;"
            title="订 单 成 交 总 额（元/人民币）"
            :value="totalAmount"
            precision="2"
          />
        </el-col>
      </el-row>
    </el-header>

    <el-main>
      <el-row>
        <el-col :span="8">
          <el-card shadow="hover" class="sale-rank">
            <div class="card-header">
              <span class="card-title">销 量 排 行 榜</span>
            </div>
            <ul v-for="(item, index) in top5BySale" :key="item.id">
              <li v-if="index + 1 <= 3">
                <span> {{ "0" + (index + 1) }} </span>
                {{ truncateText(item.productName) }} 已售：
                <span> {{ item.sales }} </span> 件
              </li>
              <li v-else>
                <span class="rankNum"> {{ "0" + (index + 1) }} </span>
                {{ truncateText(item.productName) }} 已售：
                <span> {{ item.sales }} </span> 件
              </li>
            </ul>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover" class="reservation-rank">
            <span class="card-title">预 约 排 行 榜</span>
            <div id="reservationRank"></div>
          </el-card>
        </el-col>

        <el-col :span="8" class="userBoard">
          <el-row>
            <el-col :span="12">
              <el-card shadow="hover" class="one-item-container">
                <div class="content">
                  <span class="text-left">{{ userCount }}</span>
                  <el-icon class="icon-right"><User /></el-icon>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover" class="two-item-container">
                <div class="content">
                  <span class="text-left">{{ vipCount }}</span>
                  <el-icon class="icon-right"><TrophyBase /></el-icon>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-card shadow="hover" class="three-item-container">
                <div class="content">
                  <span class="text-left">{{ femaleCount }}</span>
                  <el-icon class="icon-right"><Female /></el-icon>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover" class="four-item-container">
                <div class="content">
                  <span class="text-left">{{ maleCount }}</span>
                  <el-icon class="icon-right"><Male /></el-icon>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-main>

    <el-footer>
      <el-row>
        <el-col :span="24">
          <el-card shadow="hover">
            <span class="reservationCountByDayTitle">预 约 统 计</span>
            <div id="reservationCountByDay"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-footer>
  </el-container>
</template>


<script setup>
import { ref, onMounted, computed } from "vue";
import { useTransition } from "@vueuse/core";
import { doGet } from "../../http/httpRequest";

import * as echarts from "echarts";

onMounted(() => {
  // 获取获取一周每天的预约量并初始化预约统计图批【折线图】
  getLastWeekReservationNum();

  // 获取商品总数
  getProductCount();

  // 获取预约总数
  getReservationCount();

  // 获取闪购活动总数
  getActivityCount();

  // 获取成交总额
  getTotalAmount();

  // 获取销量前5的商品
  getSaleTop5();

  // 获取预约前5的商品【饼图】
  getReservationTop5();

  // 获取用户总数
  getUserCount();

  // 获取男性用户总数
  getMaleCount();

  // 获取女性用户总数
  getFemaleCount();

  // 获取vip用户总数
  getVipCount();
});

/**
 * @description 截断文本的计算属性
 */
const truncateText = computed({
  get: () => {
    return (productName) => {
      let truncated = productName.substring(0, 25);
      if (truncated.length < 25) {
        truncated = truncated.padEnd(25, " ");
      }
      return truncated + "...";
    };
  },
});

/************************************************* 顶部 ************************************************* */

/**
 * @description 获取商品总数
 */
const productCount = ref(0);
const getProductCount = () => {
  doGet("/product/count", {})
    .then((res) => {
      if (res.data.code === 200) {
        productCount.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取预约总数
 */
const reservationCount = ref(0);
const getReservationCount = () => {
  doGet("/reservation/count", {})
    .then((res) => {
      if (res.data.code === 200) {
        reservationCount.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取闪购活动总数
 */
const activityCount = ref(0);
const getActivityCount = () => {
  doGet("/activity/count", {})
    .then((res) => {
      if (res.data.code === 200) {
        activityCount.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取成交总金额
 */
const totalAmount = ref(0);
const source = ref(0);

totalAmount.value = useTransition(source, {
  duration: 200,
});

const getTotalAmount = () => {
  doGet("/order/amountTotal", {})
    .then((res) => {
      if (res.data.code === 200) {
        source.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/************************************************* 中间 ************************************************* */

/**
 * @description 获取销量前5的商品
 */
const top5BySale = ref([]);
const getSaleTop5 = () => {
  doGet("/product/top5", {})
    .then((res) => {
      if (res.data.code === 200) {
        top5BySale.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取预约量前5的预约活动（饼图）
 */
const top5ByReservation = ref([]);
const getReservationTop5 = () => {
  doGet("/reservation/top5", {})
    .then((res) => {
      if (res.data.code === 200) {
        top5ByReservation.value = res.data.data;
        initReservationRank();
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

const initReservationRank = () => {
  var chartDom = document.getElementById("reservationRank");
  var myChart = echarts.init(chartDom);

  var data = top5ByReservation.value.map((item, index) => ({
    value: item.count,
    name: item.reservationName,
    itemStyle: {
      normal: {
        color: getColor(index), // 获取颜色的方法
      },
    },
  }));

  var option = {
    tooltip: {
      trigger: "item",
      formatter: function (params) {
        // 显示完整信息
        return `${params.name}: ${params.value} 人 (${params.percent.toFixed(
          2
        )}%)`;
      },
      position: "bottom",
    },
    legend: {
      show: false,
    },
    toolbox: {
      show: true,
      feature: {
        mark: { show: true },
        dataView: { show: false, readOnly: false },
        restore: { show: false },
        saveAsImage: { show: true },
      },
    },
    series: [
      {
        name: "预约量前五的预约活动",
        type: "pie",
        radius: [15, 50],
        center: ["50%", "50%"],
        roseType: "area",
        itemStyle: {
          borderRadius: 5,
        },
        label: {
          show: true,
          position: "outside",
        },
        data: data,
      },
    ],
  };

  function getColor(index) {
    // 根据索引返回颜色
    const colors = ["#4dabf7", "#69db7c", "#f7e16f", "#845ef7", "#ff6b6b"];
    return colors[index % colors.length];
  }

  option && myChart.setOption(option);
};

/**
 * @description 获取用户总数量
 */
const userCount = ref(0);
const getUserCount = () => {
  doGet("/user/count", {})
    .then((res) => {
      if (res.data.code === 200) {
        userCount.value = res.data.data;
      } else {
        EMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取男性总数
 */
const maleCount = ref(0);
const getMaleCount = () => {
  doGet("/user/male", {})
    .then((res) => {
      if (res.data.code === 200) {
        maleCount.value = res.data.data;
      } else {
        EMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取女性总数
 */
const femaleCount = ref(0);
const getFemaleCount = () => {
  doGet("/user/female", {})
    .then((res) => {
      if (res.data.code === 200) {
        femaleCount.value = res.data.data;
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 获取 VIP 总数
 */
const vipCount = ref(0);
const getVipCount = () => {
  doGet("/user/vip", {})
    .then((res) => {
      if (res.data.code === 200) {
        vipCount.value = res.data.data;
      } else {
        EMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/************************************************* 底部 ************************************************* */

/**
 * @description 预 约 统 计（折线图）
 */
const weekRservation = ref([]);

const getLastWeekReservationNum = () => {
  doGet("/reservation/week", {})
    .then((res) => {
      if (res.data.code === 200) {
        weekRservation.value = res.data.data;
        initReservationCountByDay();
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

const initReservationCountByDay = () => {
  var chartDom = document.getElementById("reservationCountByDay");
  var myChart = echarts.init(chartDom);
  var option = {
    xAxis: {
      type: "category",
      data: weekRservation.value.map((item) => item.date),
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        data: weekRservation.value.map((item) => item.count),
        type: "line",
        smooth: true,
        lineStyle: {
          color: "#4dabf7",
        },
        itemStyle: {
          color: "#69db7c",
        },
      },
    ],
  };

  option && myChart.setOption(option);
};
</script>


<style scoped>
/******************************************** 顶部统计数据 **************************************/
.el-header {
  height: 100px;
  /* margin-top: 10px; */
  margin-left: 30px;
  margin-right: 30px;
  text-align: center;
}

.el-header .el-row {
  padding: 25px;
  margin-left: 5px;
  margin-right: 5px;
}

/******************************************** 中间卡片 **************************************/

.el-main .el-card {
  height: 270px;
  border: 1px var(--flash-grey-lighter-4) solid;
  border-radius: 10px;
  margin-left: 5px;
  margin-right: 5px;
}

.sale-rank {
  background-image: var(--flash-gradient-red-2-white-bottom-right);
}

.reservation-rank {
  background-image: var(--flash-gradient-blue-2-white-bottom-right);
}

.card-title {
  font-weight: bold;
  color: var(--flash-blue-lighter-3);
  font-size: 20px;
  padding-bottom: 3px;
  border-bottom: 1px var(--flash-grey-lighter-3) solid;
}

ul {
  padding-left: 0;
  list-style: none;
}

ul li {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  line-height: 25px;
}

ul li span {
  color: var(--flash-red-lighter-2);
  font-weight: bold;
}

ul li:first-of-type > span:first-child {
  padding: 3px;
}

.rankNum {
  color: var(--flash-black-lighter-3);
}

#reservationRank {
  width: 100%;
  height: 230px;
}

/******************************************** 用户数据板 **************************************/

.userBoard .el-card {
  font-size: 24px;
  height: 125px;
  line-height: 85px;
  border: none;
  border-radius: 10px;
  padding-left: 10px;
  padding-right: 10px;
}

.userBoard .el-row:last-child {
  margin-top: 20px;
}

.content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.content span {
  text-align: left;
  color: var(--flash-black-lighter-2);
}

.content .el-icon {
  text-align: right;
}

.userBoard .one-item-container {
  background-color: var(--flash-purple-lighter-4);
  color: var(--flash-purple-color);
}

.userBoard .two-item-container {
  background-color: var(--flash-green-lighter-4);
  color: var(--flash-green-color);
}

.userBoard .three-item-container {
  background-color: var(--flash-blue-lighter-4);
  color: var(--flash-blue-color);
}

.userBoard .four-item-container {
  background-color: var(--flash-red-lighter-4);
  color: var(--flash-red-color);
}

/******************************************** 底部折线图 **************************************/

.el-footer .el-card {
  border: none;
  border-radius: 10px;
  padding-left: 130px;
  height: 270px;
  background-image: var(--flash-gradient-purple-2-white-bottom-right);
}

#reservationCountByDay {
  width: 100%;
  height: 300px;
  transform: translateY(-10px) translateX(40px);
}

.reservationCountByDayTitle {
  font-size: 18px;
  font-weight: bold;
  color: var(--flash-blue-lighter-3);
  float: left;
  margin-left: 20px;
  padding-right: 5px;
  letter-spacing: 0.3em;
  writing-mode: vertical-rl;
  border-right: 1px var(--flash-yellow-lighter-3) solid;
}
</style>