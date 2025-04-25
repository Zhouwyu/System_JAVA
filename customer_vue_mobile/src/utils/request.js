import axios from "axios";
import { showToast } from 'vant';

const request = axios.create({
    // baseURL: 'http://8.138.216.212:8090',
    baseURL: 'http://localhost:8090',
    timeout: 30000 // 后台接口超时时间
});

// request拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    let accessToken = localStorage.getItem('accessToken')
    config.headers['accessToken'] = accessToken
    return config
}, error => {
    return Promise.reject(error)
});

request.interceptors.response.use(response => {
    let res = response.data;
    if (typeof res === 'string') {
        res = res ? JSON.parse(res) : res
    }
    return res
}, error => {
    if (error.response.status === 404) {
        showToast('未找到请求接口')
    } else if (error.response.status === 500) {
        showToast('系统异常，请查看后端控制台报错')
    } else {
        showToast(error.message)
    }
    return Promise.reject(error)
})

export default request