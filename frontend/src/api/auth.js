import request from './request';

// 登录接口
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

// 登出接口
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  });
}

// 刷新token接口
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  });
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'post',
    data
  });
}
