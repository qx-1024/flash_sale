import axios from "axios";

import { ElMessage } from 'element-plus'

axios.defaults.baseURL = "http://localhost:8000";

export function doGet(url, params){
    return axios({
        method: "get",
        url: url,
        params: params,
        dataType: "json"
    })
}

export function getImg(url, params){
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

export function uploadFile(url, data){
    return axios({
        method: "post",
        url: url,
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function doPost(url, data){
    return axios({
        method: "post",
        url: url,
        data: data,
        dataType: "json",
        headers: {
            'Content-Type': 'application/json'
        }
    })
}

export function doPut(url, data){
    return axios({
        method: "put",
        url: url,
        data: data,
        dataType: "json"
    })
}

export function doDelete(url, params){
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
    function(config){
        let token = window.localStorage.getItem("token")
        if(token){
            config.headers['Authorization'] = token
        }
        return config
    }, function(error){
        return Promise.reject(error)
    }
)

/**
 * @description 封装 axios 响应拦截器
 */
axios.interceptors.response.use(
    function(response) {
        // 如果响应成功，则直接返回
        return response;
    },
    function(error) {
        const originalRequest = error.config;
        const response = error.response;

        // 如果服务器返回了错误响应，并且 code 大于 900
        if (response && response.data && response.data.code > 900) {
            // 清除 token
            window.localStorage.removeItem('token');

            // 显示错误消息
            ElMessage.error('Token 错误，请重新登录！');

            // 重定向到登录页面
            window.location.href = '/login';

            return Promise.reject(error);
        }

        // 其他错误处理
        if (response) {
            // 如果服务器返回了其他错误状态码
            return Promise.reject(response);
        } else if (error.request) {
            // 如果请求已发出但没有收到响应
            console.error('No response received:', error.request);
            return Promise.reject(error);
        } else {
            // 处理设置请求时发生的错误
            console.error('Error setting up request:', error.message);
            return Promise.reject(error);
        }
    }
);