import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 刷新Token
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'post',
    data
  })
}