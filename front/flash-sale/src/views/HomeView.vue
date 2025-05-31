<template>
  <el-container>
    <!-- 轮播图 -->
    <el-header>
      <img class="logo" src="../assets/logo_white.png" alt="" />
      <div class="nav">
        <el-button
          v-if="currentUser"
          icon="User"
          size="small"
          @click="logout"
          plain
          round
          >登 出</el-button
        >
        <el-button v-else icon="User" size="small" @click="login" plain round
          >登 录</el-button
        >
      </div>

      <!-- 商品轮播 -->
      <el-carousel height="400px">
        <el-carousel-item v-for="item in swiperList" :key="item.id">
          <img height="400px" :src="item.img" />
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
            <div>
              已 售：<span class="ranking-sale">{{ item.sales }}</span>
            </div>
            <div class="ranking-price-box">
              <span class="ranking-price">￥{{ item.price }}</span>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-header>

    <!-- 商品列表 -->
    <el-main>
      <div>
        <h1>闪 购 专 区</h1>
        <el-row v-for="row in rowNum_p" :key="row">
          <el-col :span="6" v-for="col in colNum_p" :key="col">
            <el-card
              class="sale_card"
              style="max-width: 400px"
              shadow="hover"
              @click="toDetail(productList[(row - 1) * 4 + col - 1]?.productId)"
              v-if="productList[(row - 1) * 4 + col - 1]"
            >
              <img :src="productList[(row - 1) * 4 + col - 1]?.images" />

              <div class="card-header">
                <div class="card-title">
                  {{ productList[(row - 1) * 4 + col - 1]?.productName }}
                </div>
                <div class="card-sale">
                  已 售：{{ productList[(row - 1) * 4 + col - 1]?.sales }}
                </div>
                <div class="card-price">
                  ￥{{ productList[(row - 1) * 4 + col - 1]?.price }}
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <el-divider>
        <el-icon color="#ff8a6e"><star-filled /></el-icon>
      </el-divider>

      <div>
        <h1>预 约 专 区</h1>
        <el-row v-for="row in rowNum" :key="row">
          <el-col :span="6" v-for="col in colNum" :key="col">
            <el-card
              class="resever_card"
              style="max-width: 400px"
              shadow="hover"
              @click="
                toDetail(reservationProducts[(row - 1) * 4 + col - 1].productId)
              "
              v-if="reservationProducts[(row - 1) * 4 + col - 1]"
            >
              <img
                :src="reservationProducts[(row - 1) * 4 + col - 1]?.images"
              />

              <div class="card-header">
                <div class="card-title">
                  {{
                    reservationProducts[(row - 1) * 4 + col - 1]?.productName
                  }}
                </div>
                <div class="card-sale">
                  已 售：{{
                    reservationProducts[(row - 1) * 4 + col - 1]?.sales
                  }}
                </div>
                <div class="card-price">
                  ￥{{ reservationProducts[(row - 1) * 4 + col - 1]?.price }}
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
      <div>
        Copyright © 2024-2025 秋玄 广东省梅州市梅江区金山街道嘉应学院计算机学院
        版权所有
      </div>
    </el-footer>
  </el-container>
</template>


<script setup>
import { ElMessage } from "element-plus";
import { onBeforeMount, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { doGet } from "../http/httpRequest";
import { useProductStore } from "../stores/product";

const router = useRouter();
const productStore = useProductStore();

onMounted(() => {
  getSaleTop5();

  getCurrentUser();

  loadFlashProductList();

  loadReservationProductList();
});

/********************************************* 排 行 *********************************************/
/**
 * @description 获取销量前5的商品
 */
const top5BySale = ref([]);
const getSaleTop5 = () => {
  doGet("/product/top5", {})
    .then((res) => {
      if (res.data.code === 200) {
        top5BySale.value = res.data.data;
      }
    })
    .catch((err) => {
      console.log(err);
      window.localStorage.removeItem("token");
      router.push("/login");
    });
};

/****************************************** 商 品 列 表 ******************************************/

/**
 * @description 加载闪购专区的商品列表
 */
const productList = ref([]);
const rowNum_p = ref(1);
const colNum_p = ref(4);
const loadFlashProductList = () => {
  doGet("/product/flashSaleProductList", {})
    .then((res) => {
      if (res.data.code === 200) {
        productList.value = res.data.data;

        // 修改计算 rowNum 的方式
        const totalProducts = productList.value.length;
        if (totalProducts % 4 === 0) {
          rowNum_p.value = totalProducts / 4;
        } else {
          rowNum_p.value = Math.floor(totalProducts / 4) + 1;
        }
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      console.log(err);
      window.localStorage.removeItem("token");
      router.push("/login");
    });
};

/**
 * @description 加载预约专区的商品列表
 */
const reservationProducts = ref([]);
const rowNum = ref(1);
const colNum = ref(4);
const loadReservationProductList = () => {
  doGet("/reservation/products", {})
    .then((res) => {
      if (res.data.code === 200) {
        reservationProducts.value = res.data.data;

        if (reservationProducts.value.length % 4 === 0) {
          rowNum.value = reservationProducts.value.length / 4;
        } else {
          rowNum.value = Math.floor(reservationProducts.value.length / 4) + 1;
        }
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      window.localStorage.removeItem("token");
      router.push("/login");
    });
};

/**
 * @description 跳转到商品详情页
 */
const toDetail = (productId) => {
  // 存储到 pinia 中，避免在地址栏显示商品 ID
  productStore.setProductId(productId);
  router.push("/product");
};

/****************************************** 当 前 用 户 ******************************************/
const currentUser = ref({});

/**
 * @description 获取当前登录用户
 */
const getCurrentUser = () => {
  doGet("/user/currentUser", {})
    .then((res) => {
      if (res.data.code === 200) {
        currentUser.value = res.data.data;
      } else {
        ElMessage.error(res.data.msg);
      }
    })
    .catch((err) => {
      window.localStorage.removeItem("token");
      router.push("/login");
    });
};

/********************************************* 轮播 *********************************************/
/**
 * 轮播图数组
 */
const swiperList = ref([
  {
    id: 1,
    img: "http://192.168.19.128:9000/flash-sale-image-store/swiper_20250522210850317_183929165429743616.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20250522%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250522T130850Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=f5f79eb82005fd8867f30cb332348fcd4a2169951d4ad5cfb3a78de65728b841",
  },
  {
    id: 2,
    img: "http://192.168.19.128:9000/flash-sale-image-store/swiper_20250522210920069_183929290281590784.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20250522%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250522T130920Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=77e44c58b273ac6a9429aab456a75749b79f1d58c1bc17a422cec6168892bb03",
  },
  {
    id: 3,
    img: "http://192.168.19.128:9000/flash-sale-image-store/swiper_20250522210942904_183929386041745408.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20250522%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250522T130942Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ec3a0c5487cc667221d58619ee1e024fc2e697e4e2e49501c41d554c82590230",
  },
  {
    id: 4,
    img: "http://192.168.19.128:9000/flash-sale-image-store/swiper_20250522211006435_183929484846964736.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20250522%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250522T131006Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=92eafa7746bfaedbebd707d9ea36f27bb90dc77bd32743bb12368ccabb6dffd4",
  },
  {
    id: 5,
    img: "http://192.168.19.128:9000/flash-sale-image-store/swiper_20250522211027053_183929571329318912.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=riIFtW7isF1Y1UuZvgJO%2F20250522%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250522T131027Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=5b9dbb42c32ca9ad23d8e0fa351203dec8d6c2e712cd2c1bc40c457cd0f1cf98",
  },
]);

/********************************************* 登 录 *********************************************/
/**
 * @description 登录
 */
const login = () => {
  router.push("/login");
};

/********************************************* 登 出 *********************************************/
/**
 * @description 退出
 */
const logout = () => {
  doGet("/user/logout", {})
    .then((res) => {
      if (res.data.code === 200) {
        currentUser.value = {};
        window.localStorage.removeItem("token");
        router.push("/login");
      }
    })
    .catch((err) => {
      console.log(err);
      window.localStorage.removeItem("token");
      router.push("/login");
    });
};
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
  background-color: var(--flash-black-lighter-1);
}

/************************************ 轮播图 ******************************************/
.el-carousel img {
  width: 100%;
}

/************************************ 排行榜 ******************************************/
.ranking {
  height: 100px;
  font-size: 14px;
  color: var(--flash-black-color);
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

.ranking-store {
  color: var(--flash-black-lighter-2);
}

.ranking-right {
  float: right;
  line-height: 50px;
  text-align: right;
}

.ranking-sale {
  font-family: "sa-digital-number";
  font-weight: bold;
  font-size: 36px;
  color: var(--flash-red-lighter-2);
}

.ranking-price-box {
  font-family: "sa-digital-number";
  line-height: 25px;
  height: 25px;
  color: var(--flash-black-lighter-2);
  letter-spacing: 0.1em;
}

/************************************* 主体 *******************************************/
h1 {
  margin-bottom: 30px;
  padding-bottom: 5px;
  color: var(--flash-black-lighter-1);
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

.el-card img {
  height: 250px;
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
  font-size: 12px;
  line-height: 30px;
  color: #bbb;
  text-align: center;
  letter-spacing: 0.1em;
  height: 30px;
}
</style>