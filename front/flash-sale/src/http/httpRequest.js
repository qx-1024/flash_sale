import axios from "axios";

import { ElMessage } from 'element-plus'
import router from '../router/router'

axios.defaults.baseURL = "http://localhost:8000";

export function doGet(url, params) {
    return axios({
        method: "get",
        url: url,
        params: params,
        dataType: "json"
    })
}

export function getImg(url, params) {
    return axios({
        method: "get",
        url: url,
        params: params,
        responseType: 'blob',
        headers: {
            Accept: 'application/octet-stream',
        }
    })
}

export function uploadFile(url, data) {
    return axios({
        method: "post",
        url: url,
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function doPost(url, data) {
    return axios({
        method: "post",
        url: url,
        data: data,
        dataType: "json",
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })
}

export function doPut(url, data) {
    return axios({
        method: "put",
        url: url,
        data: data,
        dataType: "json",
        headers: {
            'Content-Type': 'application/json'
        }
    })
}

export function doDelete(url, params) {
    return axios({
        method: "delete",
        url: url,
        params: params,
        dataType: "json"
    })
}


/**
 * @description 封装 axios 请求拦截器
 */
axios.interceptors.request.use(
    function (config) {
        let token = window.localStorage.getItem("token")
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    }, function (error) {
        return Promise.reject(error)
    }
)

/**
 * @description 封装 axios 响应拦截器
 */
let hasShownError = false;

axios.interceptors.response.use(
    function (response) {
        // 如果响应成功，则直接返回
        return response;
    },
    function (error) {
        const originalRequest = error.config;
        const response = error.response;

        // 如果服务器返回了错误响应，并且 code 大于 900
        if (response && response.data && response.data.code > 900) {
            // 清除 token
            window.localStorage.removeItem('token');

            // 显示错误消息
            if (!hasShownError) {
                ElMessage.error(response.data.msg);
                hasShownError = true;
            }

            // 重定向到登录页面
            router.push('/login');

            return Promise.reject(error);
        }

        if (response && response.status === 400 || response.data.code === 400) {
            // 显示服务器返回的错误消息
            if (!hasShownError) {
                ElMessage.error(response.data.msg || '请求错误');
                hasShownError = true;
            }

            // 使用 Vue Router 跳转到登录页面
            router.push('/login');

            return Promise.reject(error);
        }

        if (response && response.status === 405 || response.data.code === 405) {
            // 显示服务器返回的错误消息
            if (!hasShownError) {
                ElMessage.error(response.data.msg || '请求方法错误');
                hasShownError = true;
            }

            // 使用 Vue Router 跳转到登录页面
            router.push('/login');

            return Promise.reject(error);
        }

        if (response && response.status === 409 || response.data.code === 409) {
            // 显示服务器返回的错误消息
            if (!hasShownError) {
                ElMessage.error(response.data.msg || '违反数据完整性');
                hasShownError = true;
            }

            // 使用 Vue Router 跳转到登录页面
            router.push('/login');

            return Promise.reject(error);
        }

        // 其他错误处理
        if (response) {
            // 如果服务器返回了其他错误状态码
            return Promise.reject(response);
        } else if (error.request) {
            // 如果请求已发出但没有收到响应
            if (!hasShownError) {
                ElMessage.error('服务器未响应', error.request);
                hasShownError = true;
            }
            return Promise.reject(error);
        } else {
            // 处理设置请求时发生的错误
            if (!hasShownError) {
                ElMessage.error('Error setting up request:', error.message);
                hasShownError = true;
            }
            return Promise.reject(error);
        }
    }
);