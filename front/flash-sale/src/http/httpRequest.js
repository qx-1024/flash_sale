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
    function(response){
        // token 验证未通过
        if(response.data.code > 900){            
            window.localStorage.removeItem("token");
            ElMessage.error("token 错误，请重新登录！")
            window.location.href = "/";
            return;
        }
        return response;
    }, function(error){
        return Promise.reject(error);
    }
)