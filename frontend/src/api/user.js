import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function getUsers(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: `/user/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export function assignRoles(userId, roleIds) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'post',
    data: { roleIds }
  })
}
