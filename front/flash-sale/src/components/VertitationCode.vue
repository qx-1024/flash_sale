<template>
    <img :src="captchaImageUrl" @click="getCode" style="cursor: pointer;" />
</template>


<script setup>
import { onMounted, ref, watch } from "vue";
import { getImg } from "../http/httpRequest";

import { ElMessage } from 'element-plus'

onMounted(() => {
    // 检查是否有冷却时间
    checkCooldown();
    
    // 根据冷却时间是否过期决定是否获取验证码
    if (canGetCode.value) {
        getCode();
    } else {
        ElMessage.warning("请等待 60 秒后再尝试获取验证码");
        // 如果冷却时间未过期，保持原来的验证码图片
        captchaImageUrl.value = window.localStorage.getItem('captchaImage');
    }
})

/**
 * @description 定义一个变量来控制是否可以获取验证码
 */
const canGetCode = ref(true);

/**
 * @description 使用 localStorage 存储冷却时间
 */
const cooldownKey = 'captchaCooldown';
const cooldownDuration = 60 * 1000; // 60 seconds in milliseconds

/**
 * @description 定义一个变量来保存验证码图片
 */
const captchaImageUrl = ref('');

/**
 * @description 获取验证码
 */
const getCode = () => {
    if (!canGetCode.value) {
        ElMessage.warning("请等待 60 秒后再尝试获取验证码");
        return;
    }

    // 阻止其他请求
    canGetCode.value = false;
    // 清除图片 URL
    captchaImageUrl.value = '';

    getImg('/user/loginValidateCode', {})
        .then(res => {
            const blob = new Blob([res.data], { type: 'image/jpg' });
            captchaImageUrl.value = window.URL.createObjectURL(blob);

            // 设置 60 秒后才能再次获取验证码
            setTimeout(() => {
                canGetCode.value = true;
                // 清除冷却时间标记
                window.localStorage.removeItem(cooldownKey);
            }, cooldownDuration);

            // 在成功获取验证码后存储当前时间
            localStorage.setItem(cooldownKey, Date.now().toString());
        })
        .catch(err => {
            if (canGetCode.value === false) {
                ElMessage.warning("请等待 60 秒后再尝试获取验证码");
            } else {
                ElMessage.error("获取验证码失败，请稍后重试");
            }
        });
};

/**
 * @description 检查是否有冷却时间
 */
const checkCooldown = () => {
    const lastRequestTime = window.localStorage.getItem(cooldownKey);
    if (lastRequestTime) {
        const currentTime = Date.now();
        const timeSinceLastRequest = currentTime - parseInt(lastRequestTime, 10);

        // 如果冷却时间未过，则设置 canGetCode 为 false
        if (timeSinceLastRequest < cooldownDuration) {
            canGetCode.value = false;
            // 设置定时器来等待剩余的冷却时间
            setTimeout(() => {
                canGetCode.value = true;
                window.localStorage.removeItem(cooldownKey);
            }, cooldownDuration - timeSinceLastRequest);
        } else {
            // 冷却时间已过，清除标记
            window.localStorage.removeItem(cooldownKey);
        }
    }
};

// 添加一个事件监听器来拦截页面刷新
window.addEventListener('beforeunload', (event) => {
    // 检查是否有冷却时间
    const lastRequestTime = window.localStorage.getItem(cooldownKey);
    if (lastRequestTime) {
        const currentTime = Date.now();
        const timeSinceLastRequest = currentTime - parseInt(lastRequestTime, 10);

        // 如果冷却时间未过，则阻止页面刷新
        if (timeSinceLastRequest < cooldownDuration) {
            event.preventDefault();
            return; // 不返回任何值，以避免弹窗
        }
    }
});

// 保存验证码图片 URL 到 localStorage
watch(captchaImageUrl, (newValue) => {
    if (newValue) {
        window.localStorage.setItem('captchaImage', newValue);
    }
});

</script>