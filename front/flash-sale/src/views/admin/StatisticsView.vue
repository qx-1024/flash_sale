<template>
    <el-container>
      <el-header>
        <el-row>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: var(--flash-green-color)" title="商 品 总 数" :value="productCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: var(--flash-red-lighter-2)" title="预 约 活 动 总 数" :value="reservationCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: var(--flash-green-color)" title="闪 购 活 动 总 数" :value="activityCount" />
            </el-col>
            <el-col :span="6">
                <el-statistic value-style="font-size: 28px; color: var(--flash-red-lighter-2)" title="订 单 成 交 总 额（元/人民币）" :value="totalAmount" precision="2"/>
            </el-col>
        </el-row>
      </el-header>

      <el-main>
        <el-row>
            <el-col :span="8">
                <el-card shadow="hover">
                    <div class="card-header">
                        <span class="card-title">销 量 排 行 榜</span>
                    </div>
                    <ul v-for="(item,index) in top5BySale" :key="item.id">
                        <li v-if="(index + 1 <= 3)"> 
                            <span> {{ '0' + (index + 1) }} </span> 
                            {{ truncateText(item.productName) }} 已售：
                            <span> {{ item.sales }} </span> 件
                        </li>
                        <li v-else> 
                            <span class="rankNum"> {{ '0' + (index + 1) }} </span> 
                            {{ truncateText(item.productName) }} 已售：
                            <span> {{ item.sales }} </span> 件
                        </li>
                    </ul>
                </el-card>
            </el-col>

            <el-col :span="8">
                <el-card shadow="hover">
                    <div class="card-header">
                        <span class="card-title">预 约 排 行 榜</span>
                    </div>
                    <ul v-for="item in top5ByReservation" :key="item.id">
                        <li> {{ truncateText(item.reservationName) }} -- 预约：<span> {{ item.count }} </span> 人</li>
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
import { ref, onMounted, computed } from 'vue';
import { useTransition } from '@vueuse/core'
import { doGet } from '../../http/httpRequest'

import * as echarts from 'echarts';

onMounted(() => {
    // 获取获取一周每天的预约量并初始化预约统计图
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

    // 获取预约前5的商品
    getReservationTop5();


    // 获取用户总数
    getUserCount();

    // 获取男性用户总数
    getMaleCount();

    // 获取女性用户总数
    getFemaleCount();

    // 获取vip用户总数
    getVipCount();
})


/**
 * @description 截断文本的计算属性
 */
const truncateText = computed({
    get: () => {
    return (productName) => {
        let truncated = productName.substring(0, 25);
        if (truncated.length < 25) {
            truncated = truncated.padEnd(25, ' ');
        }
        return truncated + '...';
    };
    },
});





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
 * @description 获取成交总金额
 */
const totalAmount = ref(0)
const source = ref(0)

totalAmount.value = useTransition(source,{
    duration: 200
})

const getTotalAmount = () => {
    doGet('/order/amountTotal', {})
        .then(res => {
            if (res.data.code === 200) {
                source.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err);
        })
}


/**
 * @description 预 约 统 计（折线图）
 */
const weekRservation = ref([]);

const getLastWeekReservationNum = () => {
    doGet('/reservation/week', {})
        .then(res => {
            weekRservation.value = res.data.data
            console.log(weekRservation.value.map(item => item.date));

            initReservationCountByDay();
        })
        .catch(err => {
            console.log(err);
        })
}

const initReservationCountByDay = () => {
    var chartDom = document.getElementById('reservationCountByDay');
    var myChart = echarts.init(chartDom);
    var option = {
        xAxis: {
            type: 'category',
            data: weekRservation.value.map(item => item.date)
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: weekRservation.value.map(item => item.count),
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#6741d9'
                },
                itemStyle: {
                    color: '#69db7c'
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
    height: 270px;
    border: 1px #ccc solid;
    margin-left: 5px;
    margin-right: 5px;
}

.el-main .el-card .el-card__body{
    padding-top: 0 !important;
}

.el-footer .el-card {
    padding-left: 130px;
    height: 280px;
}

.userBoard {
    font-size: 24px;
    color: var(--flash-blue-lighter-3);
}

.userBoard .el-row {
    height: 110px;
    line-height: 110px;
}

.one {
    border-bottom: 1px var(--flash-purple-color) solid;
}

.one .el-col:nth-child(1),
.two .el-col:nth-child(1) {
    border-right: 1px var(--flash-purple-color) solid;
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
    letter-spacing: .3em;
    writing-mode: vertical-rl;
    border-right: 1px var(--flash-yellow-lighter-3) solid;
}
</style>