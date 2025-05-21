import axios from 'axios';
import { Message, Loading } from 'element-ui';
import store from '@/store';
import { getToken } from '@/utils/auth';
import router from '@/router';

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8000/api', // API的基础URL
  timeout: 15000 // 请求超时时间
});

let loadingInstance;

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在请求发送之前做一些处理
    loadingInstance = Loading.service({
      lock: true,
      text: '加载中...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    // 让每个请求携带token
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    
    // 让每个请求携带当前租户ID
    const currentTenant = JSON.parse(localStorage.getItem('currentTenant'));
    if (currentTenant && currentTenant.id) {
      config.headers['X-Tenant-ID'] = currentTenant.id;
    }
    
    return config;
  },
  error => {
    // 处理请求错误
    console.log(error); // for debug
    loadingInstance && loadingInstance.close();
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    loadingInstance && loadingInstance.close();
    const res = response.data;
    
    // 如果返回的状态码不是200，说明请求出错
    if (res.code !== 200) {
      Message({
        message: res.message || '请求出错',
        type: 'error',
        duration: 5 * 1000
      });
      
      // 401: Token过期或未登录; 403: 权限不足
      if (res.code === 401 || res.code === 403) {
        // 询问用户是否重新登录
        MessageBox.confirm('您已登出，请重新登录', '确认登出', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout').then(() => {
            router.push('/login');
          });
        });
      }
      return Promise.reject(new Error(res.message || '请求出错'));
    } else {
      return res;
    }
  },
  error => {
    loadingInstance && loadingInstance.close();
    console.log('err' + error); // for debug
    Message({
      message: error.message || '请求出错',
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);

export default service;
