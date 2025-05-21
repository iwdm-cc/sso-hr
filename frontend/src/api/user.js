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
    url: '/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 获取指定用户
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: `/user/${data.id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 分配角色
export function assignRoles(userId, roleIds) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'post',
    data: { roleIds }
  })
}