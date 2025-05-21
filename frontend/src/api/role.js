import request from '@/utils/request'

export function getRoles(params) {
  return request({
    url: '/role/list',
    method: 'get',
    params
  })
}

export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: `/role/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function assignPermissions(roleId, permissionIds) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'post',
    data: { permissionIds }
  })
}

export function getRolePermissions(roleId) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'get'
  })
}
