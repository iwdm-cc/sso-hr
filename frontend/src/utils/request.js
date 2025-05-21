import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8000/api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做一些处理
    if (store.getters.token) {
      // 让每个请求携带token
      config.headers['Authorization'] = `Bearer ${getToken()}`
    }
    // 如果当前有租户信息，添加租户ID到请求头
    if (store.getters.currentTenant) {
      config.headers['X-Tenant-ID'] = store.getters.currentTenant.id
    }
    return config
  },
  error => {
    // 处理请求错误
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  /**
   * 如果您想获取http信息，如headers或status
   * 请返回response => response
  */
  response => {
    const res = response.data
    
    // 如果状态码不是200，认为请求有错误
    if (res.code !== 200) {
      Message({
        message: res.message || '错误',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: 未登录或Token过期
      if (res.code === 401) {
        // 重新登录
        MessageBox.confirm('您已登出，可以取消继续留在该页面，或者重新登录', '确认登出', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.message || '错误'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
