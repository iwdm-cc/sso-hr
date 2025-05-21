import request from '@/utils/request'

export function getPermissions(params) {
  return request({
    url: '/permission/list',
    method: 'get',
    params
  })
}

export function getPermissionById(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

export function createPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

export function updatePermission(data) {
  return request({
    url: `/permission/${data.id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}
