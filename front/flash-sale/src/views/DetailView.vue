<template>
  <div class="container">
    <el-row v-if="product">
      <!-- 左 -->
      <el-col :span="2">
        <img class="logo" src="../assets/logo.png" alt="" />

        <div class="title">BUY</div>

        <el-icon size="20" class="arrow"><Expand /></el-icon>
      </el-col>

      <!-- 中 -->
      <el-col :span="9">
        <img :src="product.images" alt="" />
      </el-col>

      <!-- 右 -->
      <el-col :span="13">
        <!-- 箭头 -->
        <el-icon size="30" class="rightArrow" @click="goback"
          ><Right
        /></el-icon>
        <!-- 矩形 -->
        <div class="shape"></div>

        <!-- 闪购名称 -->
        <div class="productName">
          {{ product.productName }}
        </div>

        <!-- 商品销量 -->
        <div class="productSales">销量：{{ product.sales }}</div>

        <!-- 商品库存 -->
        <div class="productStock">库存：{{ product.stock }}</div>

        <!-- 商品价格 -->
        <div class="productPrice">
          <p>￥{{ product.price }}</p>
        </div>

        <!-- 闪购按钮 -->
        <el-button class="buyBtn" round @click="buy">
          <el-icon size="16"><SoldOut /></el-icon>
          <span>立 即 购 买</span>
        </el-button>

        <!-- 预约按钮 -->
        <el-button
          v-if="!allowReservation"
          round
          class="noAlloweservationBtn"
          :disabled="false"
        >
          <el-icon size="18"><Star /></el-icon>
          <span>立 即 预 约</span>
        </el-button>

        <el-popconfirm
          v-else
          title="确认预约或取消预约吗?"
          confirm-button-text="确认"
          cancel-button-text="取消"
          width="200"
          confirm-button-type="danger"
          icon-color="#ff8a6e"
          icon="Bell"
          @confirm="reservation"
        >
          <template #reference>
            <el-button v-if="!isReservation" round class="reservationBtn">
              <el-icon size="18"><Star /></el-icon>
              <span>立 即 预 约</span>
            </el-button>

            <el-button v-else round class="reservationBtn">
              <el-icon size="16"><StarFilled /></el-icon>
              <span>取 消 预 约</span>
            </el-button>
          </template>
        </el-popconfirm>
      </el-col>
    </el-row>
  </div>

  <!-- 页脚 -->
  <el-footer>
    <div>
      Copyright © 2024-2025 秋玄 广东省梅州市梅江区金山街道嘉应学院计算机学院
      版权所有
    </div>
  </el-footer>
</template>


<script setup>
import { ElMessage, ElNotification } from "element-plus";
import { onBeforeMount, onMounted, onUnmounted, ref } from "vue";
import { doGet } from "../http/httpRequest";
import { useRouter } from "vue-router";
import { useProductStore } from "../stores/product";

const router = useRouter();

/********************************************* 初始化 *******************************************/
const productStore = useProductStore();
const productId = ref(null);
const product = ref(null);

onMounted(() => {
  // 获取商品 ID
  productId.value = productStore.currentProductId;

  loadPorductDetail();

  checkReservation();
});

/**
 * @description 在页面销毁前，清除当前商品 ID
 */
onUnmounted(() => {
  productStore.currentProductId = null;
  isReservation.value = false;
});

/**
 * @description 根据商品 ID 加载商品详情
 */
const loadPorductDetail = () => {
  doGet("/product/one", {
    productId: productId.value,
  })
    .then((res) => {
      if (res.data.code === 200) {
        product.value = res.data.data;
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

/**
 * @description 返回上一页
 */
const goback = () => {
  router.go(-1);
};

/********************************************* 预约 *******************************************/
// 当前用户是否已预约
const isReservation = ref(false);
// 当前闪购是否允许进行预约
const allowReservation = ref(false);

/**
 * @description 检查该商品对应的预约活动是否还在进行中
 */
const checkReservation = () => {
  doGet("/reservation/allowReservation", {
    productId: productId.value,
  }).then((res) => {
    if (res.data.code == 200) {
      allowReservation.value = res.data.data;
    }
  });
};

/**
 * @description 预约
 */
const reservation = () => {
  isReservation.value = !isReservation.value;

  // 调用后台接口，进行预约
  // 后台需要一个判断当前用户是否已预约的接口，用于初始化按钮的样式

  if (isReservation.value) {
    ElNotification({
      title: "预约结果",
      message: "恭喜您，确认预约成功",
      type: "success",
      showClose: false,
    });
  } else {
    ElNotification({
      title: "预约结果",
      message: "恭喜您，取消预约成功",
      type: "error",
      showClose: false,
    });
  }
};

/********************************************* 闪购 *******************************************/
const buy = () => {
  // 未预约
  if (isReservation.value === false) {
    // 提示未预约
    ElMessage.error("您尚未预约该闪购活动，请先进行预约！");
  } else {
    // 向后端发送请求进行闪购
    ElMessage({
      message: "闪购成功",
      type: "success",
    });
  }
};
</script>


<style scoped>
.container {
  position: relative;
  height: 100vh;
  width: 100%;
  background-image: url(../images/bg3.png);
  background-size: cover;
  z-index: 0;
}

.logo {
  width: 50px;
  height: 50px;
  position: fixed;
  top: -350px;
  left: 30px;
}

.el-row {
  margin: calc(10vh) auto;
  width: calc(80vw);
}

.el-col {
  color: #fff;
  height: calc(80vh);
}

.el-col-2 {
  color: var(--flash-red-lighter-3);
  position: relative;
  background-color: var(--flash-grey-lighter-4);
  border-radius: 30px 0 0 30px;
}

.logo {
  width: 50px;
  height: 50px;
  font-weight: bold;
  position: absolute;
  top: 10px;
  left: 25%;
}

.title {
  font-weight: bold;
  position: absolute;
  top: 45%;
  left: 35%;
  padding-bottom: 3px;
  border-bottom: var(--flash-red-lighter-3) 2px dashed;
}

.arrow {
  position: absolute;
  bottom: 30px;
  left: 40%;
}

.el-col-9 {
  border-top: 1px dashed #aaa;
  border-bottom: 1px dashed #aaa;
}

img {
  margin-top: 25%;
  width: 100%;
}

.el-col-13 {
  color: var(--flash-black-lighter-2);
  letter-spacing: 0.1em;
  padding-left: 100px;
  padding-right: 100px;
  border-top: 1px dashed #aaa;
  border-bottom: 1px dashed #aaa;
  border-right: 1px dashed #aaa;
  border-radius: 0 30px 30px 0;
}

/* 箭头 */
.rightArrow {
  position: absolute;
  top: 50px;
  right: 130px;
  color: var(--flash-red-lighter-3);
  animation: move 2s ease-in-out infinite;
  z-index: 1;
}

/* 矩形 */
.shape {
  position: absolute;
  top: 50px;
  right: 100px;
  background-color: var(--flash-grey-lighter-3);
  width: 15px;
  height: 200px;
  z-index: -1;
}

/* 商品名称 */
.productName {
  margin-top: 150px;
  margin-bottom: 20px;
  font-size: 24px;
}

/* 库存与价格 */
.productSales,
.productStock {
  font-size: 14px;
  color: var(--flash-black-lighter-3);
}

/* 库存 */
.productStock {
  margin-top: 5px;
}

/* 价格 */
.productPrice {
  color: var(--flash-red-lighter-1);
  font-size: 30px;
  font-weight: bold;
}

.el-button {
  float: right;
  margin-top: 220px;
  border: none;
  color: #fff;
  background-color: var(--flash-green-lighter-1);
}

/* 购买按钮 */
.el-button .el-icon {
  margin-right: 5px;
}

.el-button:hover {
  background-color: var(--flash-green-lighter-2);
}

/* 允许预约按钮 */
.reservationBtn {
  background-color: var(--flash-red-lighter-2);
  margin-right: 10px;
}

.reservationBtn:hover {
  background-color: var(--flash-red-lighter-3);
}

/* 不允许预约按钮 */
.noAlloweservationBtn {
  background-color: var(--flash-grey-lighter-1);
  margin-right: 10px;
}

.noAlloweservationBtn:hover {
  background-color: var(--flash-grey-lighter-2);
}

/* 箭头移动动画 */
@keyframes move {
  0% {
    transform: translateX(0); /* Start position */
  }
  50% {
    transform: translateX(10px); /* Move right by 10px */
  }
  100% {
    transform: translateX(0); /* Return to start position */
  }
}

/************************************* 页脚 *******************************************/
.el-footer {
  position: absolute;
  bottom: 1px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 13px;
  height: 30px;
  line-height: 30px;
  color: #2d405988;
}
</style>