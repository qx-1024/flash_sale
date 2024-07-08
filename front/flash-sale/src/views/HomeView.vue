<template>
    <el-container>
        <!-- 轮播图 -->
        <el-header>
            <img class="logo" src="../assets/logo_white.png" alt="">
            <div class="nav">
                <el-button v-if="currentUser" icon="User" size="small" @click="logout" plain round>登 出</el-button>
                <el-button v-else icon="User" size="small" @click="login" plain round>登 录</el-button>
            </div>

            <!-- 商品轮播 -->
            <el-carousel height="400px">
                <el-carousel-item v-for="item in swiperList" :key="item.id">
                    <img height="400px" :src="item.img">
                </el-carousel-item>
            </el-carousel>

            <!-- 销量排行 -->
            <el-carousel class="ranking" height="200px" direction="vertical">
                <el-carousel-item v-for="item in top5BySale" :key="item.id">
                    <div class="ranking-left">
                        <div class="ranking-title">{{ item.productName }}</div>
                        <div class="ranking-store">库 存：{{ item.stock }}</div>
                    </div>
                    <div class="ranking-right">
                        <div>已 售：<span class="ranking-sale">{{ item.sales }}</span></div>
                        <div class="ranking-price-box"><span class="ranking-price">￥{{ item.price }}</span></div>
                    </div>
                </el-carousel-item>
            </el-carousel>
        </el-header>

        <!-- 商品列表 -->
        <el-main>
            <div v-if="productList.length > 0">
                <h1>闪 购 专 区</h1>
                <el-row v-for="row in rowNum" :key="row">
                    <el-col :span="6" v-for="col in colNum" :key="col">
                        <el-card style="max-width: 400px" shadow="hover" @click="toDetail(productList[(row - 1) * 4 + col - 1].productId)">
                            <img :src="productList[(row - 1) * 4 + col - 1].images" >

                            <div class="card-header">
                                <div class="card-title">
                                    {{ productList[(row - 1) * 4 + col - 1].productName }}
                                </div>
                                <div class="card-sale">
                                    已 售：{{ productList[(row - 1) * 4 + col - 1].sales }}
                                </div>
                                <div class="card-price">
                                    ￥{{ productList[(row - 1) * 4 + col - 1].price }}
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </div>

            <el-divider>
                <el-icon color="#ff8a6e"><star-filled /></el-icon>
            </el-divider>

            <div v-if="productList.length > 0">
                <h1>预 约 专 区</h1>
                <el-row v-for="row in rowNum" :key="row">
                    <el-col :span="6" v-for="col in colNum" :key="col">
                        <el-card style="max-width: 400px" shadow="hover" @click="toDetail(productList[(row - 1) * 4 + col - 1].productId)">
                            <img :src="productList[(row - 1) * 4 + col - 1].images" >

                            <div class="card-header">
                                <div class="card-title">
                                    {{ productList[(row - 1) * 4 + col - 1].productName }}
                                </div>
                                <div class="card-sale">
                                    已 售：{{ productList[(row - 1) * 4 + col - 1].sales }}
                                </div>
                                <div class="card-price">
                                    ￥{{ productList[(row - 1) * 4 + col - 1].price }}
                                </div>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </div>
        </el-main>

        <el-backtop :right="20" :bottom="20" :visibility-height="350" />

        <!-- 页脚 -->
        <el-footer>
            <div>Copyright © 2024-2025 秋玄 广东省梅州市梅江区金山街道嘉应学院计算机学院 版权所有 </div>
        </el-footer>
    </el-container> 
</template>


<script setup>
import { onBeforeMount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { doGet } from '../http/httpRequest'
import { useProductStore } from '../stores/product'

const router = useRouter()
const productStore = useProductStore()

onMounted(() => {
    getSaleTop5();

    getCurrentUser();

    loadProductList();
})

/********************************************* 排 行 *********************************************/
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
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}


/****************************************** 商 品 列 表 ******************************************/
const productList = ref([])
const rowNum = ref(1)
const colNum = ref(4)

/**
 * @description 加载闪购的商品列表
 */
const loadProductList = () => {
    doGet('/product/flashSaleProductList',{})
        .then(res => {
            if(res.data.code === 200){
                productList.value = res.data.data

                if(productList.value.length % 4 === 0){
                    rowNum.value = productList.value.length / 4
                }else{
                    rowNum.value = Math.floor(productList.value.length / 4) + 1
                }
            }
        })
        .catch(err => {
            console.log(err)
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}

/**
 * @description 跳转到商品详情页
 */
const toDetail = (productId) => {
    // 存储到 pinia 中，避免在地址栏显示商品 ID
    productStore.setProductId(productId)
    router.push('/product')
}

/****************************************** 当 前 用 户 ******************************************/
const currentUser = ref({})

/**
 * @description 获取当前登录用户
 */
const getCurrentUser = () => {
    doGet('/user/currentUser', {})
        .then(res => {
            if (res.data.code === 200) {
                currentUser.value = res.data.data;
            }
        })
        .catch(err => {
            console.log(err)
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}

/********************************************* 轮播 *********************************************/
/**
 * 轮播图数组
 */
const swiperList = ref([
  {id: 1, img: "http://192.168.182.128:9000/flash-sale-image-store/swiper_20240701100209668_65985334298943488.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20240701%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240701T020209Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=693368b31798d72c52029dbebfe03026c9be4ebe415d5c39f9ba73edcd39cb3f"},
  {id: 2, img: "http://192.168.182.128:9000/flash-sale-image-store/swiper_20240701100253937_65985520068861952.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20240701%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240701T020253Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=72c749c0c3198f4964e5991dc32e4c2fb33640c29af353e76a8eef19076b7b4b"},
  {id: 3, img: "http://192.168.182.128:9000/flash-sale-image-store/swiper_20240701100311397_65985593301409792.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20240701%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240701T020311Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=57f8ebe4157caba59c617a01a112e5586b9ccc23a55a43283ef2c27c9f9fc180"},
  {id: 4, img: "http://192.168.182.128:9000/flash-sale-image-store/swiper_20240701100332448_65985681591508992.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20240701%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240701T020332Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=5b63ff1cec7d6ad2737813334c7ebb1f73dc2174e43bfc01b9c3f990ce8084d8"},
  {id: 5, img: "http://192.168.182.128:9000/flash-sale-image-store/swiper_20240701100349311_65985752324251648.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20240701%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240701T020349Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=c5915fc3023c17529fb5d51aed404ee482d5e34d2a9e8e5709f4900c41aca151"},
])


/********************************************* 登 录 *********************************************/
/**
 * @description 登录
 */
const login = () => {
    router.push('/login')
}

/********************************************* 登 出 *********************************************/
/**
 * @description 退出
 */
const logout = () => {
    doGet('/user/logout', {})
        .then(res => {
            if (res.data.code === 200) {
                currentUser.value = {};
                window.localStorage.removeItem('token')
                router.push('/login')
            }
        })
        .catch(err => {
            console.log(err)
            window.localStorage.removeItem('token')
            router.push('/login')
        })
}
</script>


<style scoped>
/************************************* 头部 *******************************************/
.el-header {
    height: 550px;
    padding: 0;
}

.logo {
    height: 50px;
    float: left;
    margin-left: 30px;
}

.nav {
    height: 50px;
    line-height: 50px;
    text-align: right;
    padding-right: 30px;
    background-color: #2d4059aa;
}

.nav sapn{
    font-size: 14px;
    letter-spacing: .2em;
    color: #fff;
}

.el-link {
    color: #fff;
}

.el-backtop {
    color: #ff8a6e;
}

/************************************ 轮播图 ******************************************/
.el-carousel img {
    width: 100%;
}

/************************************ 排行榜 ******************************************/
.ranking {
    height: 100px;
    font-size: 14px;
    color: #2d4059;
    margin-top: 10px;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 30px;
    padding-right: 30px;
}

.ranking-left {
    float: left;
    line-height: 50px;
}

.ranking-title {
    font-size: 32px;
}

.ranking-store{
    color: #2d4059aa;
}

.ranking-right {
    float: right;
    line-height: 50px;
    text-align: right;
}

.ranking-sale {
    font-weight: bold;
    font-size: 36px;
    color: #ff8a6e;
}

.ranking-price-box {
    line-height: 25px;
    height: 25px;
    color: #2d4059aa;
    letter-spacing: .1em;
}

/************************************* 主体 *******************************************/
h1 {
    margin-bottom: 30px;
    padding-bottom: 5px;
    color: #32dadd;
    font-size: 36px;
    text-align: center;
}

.el-row {
    margin-bottom: 30px;
}

.el-col {
    padding-left: 30px;
}

.el-card {
    border-radius: 10px;
    border: #2d405944 solid 1px;
}

.card-title,
.card-price {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.card-title {
    padding-top: 15px;
    border-top: 1px dashed #2d4059aa;
}

.card-price {
    font-size: 24px;
    font-weight: bold;
    text-align: right;
}

.card-sale {
    color: #2d4059aa;
    padding-bottom: 5px;
    margin-top: 5px;
    margin-bottom: 5px;
    font-size: 13px;
}

.card-btn {
    margin-top: 10px;
    width: 100%;
    letter-spacing: 1em;
    color: #fff;
    background-color: #32dadd;
}

.card-btn:hover {
    background-color: #59f9fc;
}

.el-main img {
    margin-top: 10px;
    width: 100%;
}

.el-divider {
    margin-top: 80px;
    margin-bottom: 50px;
}

/************************************* 页脚 *******************************************/
.el-footer {
    font-size: 14px;
    line-height: 30px;
    color: #bbb;
    text-align: center;
    letter-spacing: .1em;
    height: 30px;
}
</style>