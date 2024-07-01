<template>
    <el-container>
      <el-header>
        <el-row>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: #f57170" title="商 品 总 数" :value="productCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: #f57170" title="预 约 活 动 总 数" :value="reservationCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: #f57170" title="闪 购 活 动 总 数" :value="activityCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: #f57170" title="订 单 成 交 总 额（单位：元/人民币）" :value="activityCount" />
            </el-col>
        </el-row>
      </el-header>

      <el-main>
        <el-row>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span class="card-title">销 量 排 行 榜</span>
                        </div>
                    </template>
                    <ul v-for="item in top5BySale" :key="item.id">
                        <li> {{ item.productName }} -- 已售：<span> {{ item.sales }} </span></li>
                    </ul>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover" class="userBoard">
                    <el-row class="one">
                        <el-col :span="12">
                            <el-icon><User /></el-icon>
                            <span>{{ userCount }}</span>
                        </el-col>
                        <el-col :span="12">
                            <el-icon><TrophyBase/></el-icon>
                            <span>{{ vipCount }}</span>
                        </el-col>
                    </el-row>
                    <el-row class="two">
                        <el-col :span="12">
                            <el-icon><Female/></el-icon>
                            <span>{{ femaleCount }}</span>
                        </el-col>
                        <el-col :span="12">
                            <el-icon><Male/></el-icon>
                            <span>{{ maleCount }}</span>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span class="card-title">预 约 排 行 榜</span>
                        </div>
                    </template>
                    <ul v-for="item in top5ByReservation" :key="item.id">
                        <li> {{ item.reservationName }} -- 预约：<span> {{ item.count }} </span></li>
                    </ul>
                </el-card>
            </el-col>
        </el-row>
      </el-main>


      <el-footer>
        <el-row>
            <el-col :span="16">
                <el-card shadow="hover">
                    <span class="reservationCountByDayTitle">预 约 统 计</span>
                    <div id="reservationCountByDay"></div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span class="card-title">闪 购 排 行 榜</span>
                        </div>
                    </template>
                    <ul v-for="item in top5ByActivity" :key="item.id">
                        <li>{{ item.activityName }} -- 参与：<span> {{ item.activityCount }} </span> 人</li>
                    </ul>
                </el-card>
            </el-col>
        </el-row>
      </el-footer>
    </el-container>    
</template>


<script setup>
import { ref, onMounted } from 'vue';
import { doGet } from '../../http/httpRequest'

import * as echarts from 'echarts';




onMounted(() => {
    initReservationCountByDay();

    getProductCount();

    getReservationCount();

    getActivityCount();

    getSaleTop5();

    getReservationTop5();

    getActivityTop5();

    getUserCount();

    getMaleCount();

    getFemaleCount();

    getVipCount();
})

/**
 * @description 获取商品总数
 */
const productCount = ref(0)
const getProductCount = () => {
    doGet('/product/count', {})
        .then(res => {
            if(res.data.code === 200){
                productCount.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取预约总数
 */
const reservationCount = ref(0)
const getReservationCount = () => {
    doGet('/reservation/count', {})
        .then(res => {
            if(res.data.code === 200){
                reservationCount.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取闪购活动总数
 */
const activityCount = ref(0)
const getActivityCount = () => {
    doGet('/activity/count', {})
        .then(res => {
            if(res.data.code === 200){
                activityCount.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 预 约 统 计（折线图）
 */
const initReservationCountByDay = () => {
    var chartDom = document.getElementById('reservationCountByDay');
    var myChart = echarts.init(chartDom);
    var option = {
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [820, 932, 901, 934, 1290, 1330, 1320],
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#f73859'
                },
                itemStyle: {
                    color: '#f73859'
                },
            }
        ]
    };

    option && myChart.setOption(option);
}


/**
 * @description 获取销量前5的商品
 */
const top5BySale = ref([])
const getSaleTop5 = () => {
    doGet('/product/top5', {})
        .then(res => {
            if (res.data.code === 200) {
                top5BySale.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err)
        })
}

/**
 * @description 获取预约量前5的预约活动
 */
const top5ByReservation = ref([])
const getReservationTop5 = () => {
    doGet('/reservation/top5', {})
        .then(res => {
            if(res.data.code === 200){
                top5ByReservation.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取参与人数前5的闪购活动
 */
const top5ByActivity = ref([])
const getActivityTop5 = () => {
    doGet('/activity/top5', {})
        .then(res => {
            if(res.data.code ===  200){
                top5ByActivity.value = res.data.data
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取用户总数量
 */
const userCount = ref(0)
const getUserCount = () => {
    doGet('/user/count', {})
        .then(res => {
            if(res.data.code === 200){
                userCount.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取男性总数
 */
const maleCount = ref(0)
const getMaleCount = () => {
    doGet('/user/male', {})
        .then(res => {
            maleCount.value = res.data.data
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取女性总数
 */
const femaleCount = ref(0)
const getFemaleCount = () => {
    doGet('/user/female', {})
        .then(res => {
            if(res.data.code === 200){
                femaleCount.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err);
        })
}

/**
 * @description 获取 VIP 总数
 */
const vipCount = ref(0)
const getVipCount = () => {
    doGet('/user/vip', {})
        .then(res => {
            if(res.data.code === 200){
                vipCount.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err);
        })
}


</script>


<style scoped>
.el-header {
    height: 100px;
    margin-top: 10px;
    margin-left: 30px;
    margin-right: 30px;
    text-align: center;
}

.el-header .el-row {
    padding: 25px;
    margin-left: 5px;
    margin-right: 5px;
}

.el-main .el-card {
    height: 260px;
    border: 1px #ccc solid;
    margin-left: 5px;
    margin-right: 5px;
}

.el-main .el-card .el-card__body{
    padding-top: 0 !important;
}

.el-footer .el-card {
    height: 280px;
}

.userBoard {
    font-size: 24px;
}

.userBoard .el-row {
    height: 110px;
    line-height: 110px;
}

.one {
    border-bottom: 1px #32dadd solid;
}

.one .el-col:nth-child(1),
.two .el-col:nth-child(1) {
    border-right: 1px #32dadd solid;
}

.one .el-col,
.two .el-col {
    height: 100%;
    display: flex; /* 使用 Flexbox 布局 */
    align-items: center; /* 垂直方向居中对齐 */
    padding-left: 35px;
}

.el-icon {
    margin-right: 5px;
    padding: 5px;
    color: #32dadd;
}

.card-title {
    font-weight: bold;
    color: #32dadd;
    font-size: 20px;
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
    line-height: 15px;
}

ul li span {
    color: #ea5455;
}

#reservationCountByDay {
    width: 100%;
    height: 300px;
    transform: translateY(-10px) translateX(40px);
}

.reservationCountByDayTitle {
    font-size: 18px;
    font-weight: bold;
    color: #32dadd;
    float: left;
    margin-left: 20px;
    padding-right: 5px;
    letter-spacing: .3em;
    writing-mode: vertical-rl;
    border-right: 1px #32dadd solid;
}
</style>