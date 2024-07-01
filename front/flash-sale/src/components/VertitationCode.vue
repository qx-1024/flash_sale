<template>
<img :src="captchaImageUrl" @click="getCode" style="cursor: pointer;" />
</template>


<script setup>
import { onMounted, ref } from "vue";
import { getImg } from "../http/httpRequest";

import { ElMessage } from 'element-plus'

onMounted(() => {
    getCode();
})

const captchaImageUrl = ref('');
/**
 * @description 获取验证码
 */
const getCode = () => {
    // 清除图片 URL
    captchaImageUrl.value = '';
    getImg('/user/loginValidateCode', {})
        .then(res => {
            const blob = new Blob([res.data], { type: 'image/jpg' });
            captchaImageUrl.value = window.URL.createObjectURL(blob);
        })
        .catch(err => {
            ElMessage.error("获取验证码失败");
            console.log(err);
        });
};
</script>