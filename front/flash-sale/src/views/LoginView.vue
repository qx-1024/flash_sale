<template>
  <div class="container">
    <!-- 登录窗口 -->
    <el-card class="loginCard" shadow="hover" v-show="isLogin">
      <el-form
        label-width="auto"
        ref="form"
        :model="loginQuery"
        :scroll-to-error="true"
        :rules="loginRules"
      >
        <el-form-item>
          <h1>登 录</h1>
        </el-form-item>

        <el-form-item label="账  号" prop="account">
          <el-input v-model="loginQuery.account" placeholder="请输入账号" />
        </el-form-item>

        <el-form-item label="密  码" prop="password">
          <el-input
            v-model="loginQuery.password"
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input
            style="width: 180px; margin-right: 5px"
            v-model="loginQuery.code"
            placeholder="请填写验证码"
          />
          <Vertitatioin />
        </el-form-item>

        <el-form-item class="options">
          <el-space wrap :size="310">
            <el-link type="error" :underline="false" @click="forget"
              >忘记密码？</el-link
            >
            <el-link type="primary" :underline="false" @click="toRegister"
              >注册账号</el-link
            >
          </el-space>
        </el-form-item>

        <el-form-item>
          <el-button class="comfirmBtn" round @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 注册窗口 -->
    <el-card class="registerCard" shadow="hover" v-show="isRegister">
      <el-form
        ref="registryForm"
        label-width="auto"
        :model="registryQuery"
        :scroll-to-error="true"
        :rules="registerRules"
      >
        <el-form-item>
          <h1>注 册</h1>
        </el-form-item>

        <el-form-item label="账 号" prop="account">
          <el-input v-model="registryQuery.account" placeholder="请输入账号" />
        </el-form-item>

        <el-form-item label="密 码" prop="password">
          <el-input
            v-model="registryQuery.password"
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-form-item label="姓 名" prop="realName">
          <el-input v-model="registryQuery.realName" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="昵 称" prop="nickName">
          <el-input v-model="registryQuery.nickName" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="手 机" prop="phoneNumber">
          <el-input
            v-model="registryQuery.phoneNumber"
            placeholder="请输入手机号码"
          />
        </el-form-item>

        <el-form-item label="地 址" prop="address">
          <el-input v-model="registryQuery.address" placeholder="请输入地址" />
        </el-form-item>

        <el-form-item label="性 别" prop="gender">
          <el-select v-model="registryQuery.gender" placeholder="请选择性别">
            <el-option
              v-for="item in genderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-space wrap :size="310">
            <el-button class="cacelBtn" round @click="cancelRegister"
              >取 消</el-button
            >
            <el-button class="comfirmBtn" round @click="register"
              >注 册</el-button
            >
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 忘记密码窗口 -->
    <el-card class="forgetPasswordCard" shadow="hover" v-show="forgetPassword">
      <el-form
        :model="forgetPasswordQuery"
        :scroll-to-error="true"
        ref="forgetPasswordForm"
        label-width="auto"
      >
        <el-form-item>
          <h1>修 改 密 码</h1>
        </el-form-item>

        <el-form-item label="账 号">
          <el-input
            v-model="forgetPasswordQuery.account"
            placeholder="请输入账号"
          />
        </el-form-item>

        <el-form-item label="新 密 码">
          <el-input
            v-model="forgetPasswordQuery.newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>

        <el-form-item label="确 认 密 码">
          <el-input
            v-model="forgetPasswordQuery.confirmPassword"
            type="password"
            placeholder="请输入确认密码"
          />
        </el-form-item>

        <el-form-item>
          <el-space wrap :size="310">
            <el-button class="cacelBtn" round @click="cancelUpdate"
              >取 消</el-button
            >
            <el-button class="comfirmBtn" round @click="confirmCommit"
              >确 认</el-button
            >
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>


<script setup>
import { onMounted, onUnmounted, ref, toRefs } from "vue";
import { doGet, doPost, doPut, getImg } from "../http/httpRequest";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import axios from "axios";

import Vertitatioin from "../components/VertitationCode.vue";

const router = useRouter();

/**
 * @description 性别选项
 */
const genderOptions = ref([
  { label: "男", value: 0 },
  { label: "女", value: 1 },
]);

/*********************************************** 校 验 规 则 *********************************************/
/**
 * @description 登录表单验证规则
 */
const loginRules = ref({
  account: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
  code: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { min: 4, max: 4, message: "长度为 4 个字符", trigger: "blur" },
  ],
});

/**
 * @description 注册表单验证规则
 */
const registerRules = ref({
  account: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
  realName: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  nickName: [
    { required: true, message: "请输入昵称", trigger: "blur" },
    { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" },
  ],
  phoneNumber: [
    { required: true, message: "手机号不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "手机号格式错误", trigger: "blur" },
  ],
  address: [
    { required: true, message: "请输入地址", trigger: "blur" },
    { min: 3, max: 255, message: "长度在 3 到 255 个字符", trigger: "blur" },
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
});

/*********************************************** 窗 口 显 示 *********************************************/

/**
 * @description 登录窗口是否显示
 */
let isLogin = ref(true);

/**
 * @description 注册窗口是否显示
 */
let isRegister = ref(false);

/**
 * @description 忘记密码窗口是否显示
 */
let forgetPassword = ref(false);

/*********************************************** 加 密 算 法 *********************************************/
/**
 * @description 加密算法（SHA-256）
 */
const sha_256_encrypt = async (str) => {
  const encoder = new TextEncoder();
  const data = encoder.encode(str);
  const hash = await crypto.subtle.digest("SHA-256", data);
  const hexHash = Array.from(new Uint8Array(hash))
    .map((byte) => byte.toString(16).padStart(2, "0"))
    .join("");
  return hexHash;
};

/************************************************* 登 录 ************************************************/
/**
 * @description 登录表单对象
 */
const form = ref(null);
let loginQuery = ref({
  account: "",
  password: "",
  code: "",
});

/**
 * @description 登录
 * 1. 验证表单是否合法
 * 2. 获取表单数据
 * 3. 加密密码
 * 4. 发送请求
 * 5. 提示结果
 */
const login = () => {
  form.value.validate((isValid) => {
    if (isValid) {
      sha_256_encrypt(loginQuery.value.password).then((encryptedPassword) => {
        doGet("/user/login", {
          account: loginQuery.value.account,
          password: encryptedPassword,
          code: loginQuery.value.code,
        }).then((resp) => {
          loginQuery.value = {};
          if (resp.data.code === 200) {
            // 将后端返回的 token 存储到 localStorage 中
            localStorage.setItem("token", resp.data.data);
            ElMessage({
              message: "登录成功",
              type: "success",
            });
            // 跳转到首页
            router.push("/home");
          } else {
            ElMessage.error("登录失败");
            // 刷新页面
            window.location.reload();
          }
        });
      });
    }
  });
};

/**
 * @description 登录回车键按下事件
 */
const handleKeyDown = (event) => {
  if (event.key === "Enter") {
    // Prevent the default Enter key behavior (e.g., form submission)
    event.preventDefault();

    // Trigger the login method
    login();
  }
};

onMounted(() => {
  document.addEventListener("keydown", handleKeyDown);
});

// Don't forget to clean up the event listener when component is unmounted
onUnmounted(() => {
  document.removeEventListener("keydown", handleKeyDown);
});

/*********************************************** 注 册 *********************************************/
/**
 * @description 注册表单对象
 */
const registryForm = ref(null);
let registryQuery = ref({
  realName: "",
  account: "",
  password: "",
  phoneNumber: "",
  nickName: "",
  address: "",
  gender: "",
  userType: "",
  lastLoginAt: "",
  createAt: "",
});

/**
 * @description 关闭登录窗口，显示注册窗口
 */
const toRegister = () => {
  registryQuery.value = {};
  isLogin.value = false;
  isRegister.value = true;
};

/**
 * @description 取消注册，关闭注册窗口，显示登录窗口
 */
const cancelRegister = () => {
  isLogin.value = true;
  isRegister.value = false;
};

/**
 * @description 关闭注册窗口，显示登录窗口
 * 1. 验证表单是否合法
 * 2. 获取表单数据
 * 3. 加密密码
 * 4. 发送请求
 * 5. 提示结果
 */
const register = () => {
  isLogin.value = true;
  isRegister.value = false;

  // 发送请求到后端
  registryForm.value.validate((isValid) => {
    if (isValid) {
      registryQuery.value.userType = "普通用户";

      // 密码加密
      sha_256_encrypt(registryQuery.value.password).then(
        (encryptedPassword) => {
          registryQuery.value.password = encryptedPassword;

          let json = JSON.stringify(registryQuery.value);

          doPost("/user/save", json).then((resp) => {
            if (resp.data.code === 200) {
              ElMessage({
                message: "注册成功",
                type: "success",
              });
            } else {
              ElMessage.error("注册失败");
            }
          });
        }
      );
    }
  });
};

/*********************************************** 修 改 密 码 *********************************************/
/**
 * @description 忘记密码表单对象
 */
const forgetPasswordForm = ref(null);
let forgetPasswordQuery = ref({
  account: "",
  newPassword: "",
  confirmPassword: "",
});

/**
 * @description 关闭登录窗口，显示忘记密码窗口
 */
const forget = () => {
  isLogin.value = false;
  forgetPassword.value = true;
};

/**
 * @description 取消修改，关闭忘记密码窗口，显示登录窗口
 */
const cancelUpdate = () => {
  isLogin.value = true;
  forgetPassword.value = false;
};

/**
 * @description 关闭忘记密码窗口，显示登录窗口
 */
const confirmCommit = () => {
  isLogin.value = true;
  forgetPassword.value = false;

  forgetPasswordForm.value.validate((isValid) => {
    if (isValid) {
      sha_256_encrypt(forgetPasswordQuery.value.newPassword).then(
        (newEncryptPassword) => {
          sha_256_encrypt(forgetPasswordQuery.value.confirmPassword).then(
            (confirmEncryptPassword) => {
              if (newEncryptPassword === confirmEncryptPassword) {
                doPut("/user/updatePassWord", {
                  account: forgetPasswordQuery.value.account,
                  password: newEncryptPassword,
                  confirmPassword: confirmEncryptPassword,
                }).then((resp) => {
                  forgetPasswordQuery.value = {};
                  if (resp.data.code === 200) {
                    ElMessage({
                      message: "修改成功",
                      type: "success",
                    });
                  } else {
                    ElMessage.error("修改失败");
                  }
                });
              }
            }
          );
        }
      );
    }
  });
};
</script>


<style scoped>
.container {
  width: 100%;
  height: calc(100vh - 180px);
  background-image: url(../images/bg.png);
  background-size: cover;
  padding-top: 180px;
}

.loginCard {
  margin: 0 auto;
  width: 480px;
  height: 340px;
  border-radius: 10px;
  opacity: 0.7;
}

.registerCard {
  margin: 0 auto;
  width: 480px;
  height: 500px;
  border-radius: 10px;
  opacity: 0.7;
}

.forgetPasswordCard {
  margin: 0 auto;
  width: 480px;
  height: 290px;
  border-radius: 10px;
  opacity: 0.7;
}

.el-form {
  text-align: center;
}

h1 {
  margin-top: 0;
  width: 100%;
  color: var(--flash-blue-color);
}

.options {
  width: 100%;
}

.comfirmBtn {
  width: 100%;
  border: none;
  color: var(--falsh-pure-white);
  background-color: var(--flash-blue-lighter-2);
}

.comfirmBtn:hover {
  background-color: var(--flash-blue-lighter-3);
}

.cacelBtn {
  border: none;
  color: var(--falsh-pure-white);
  background-color: var(--flash-red-lighter-2);
}

.cacelBtn:hover {
  background-color: var(--flash-red-lighter-3);
}
</style>