import request from '@/utils/request'

export function getTenants(params) {
  return request({
    url: '/tenant/list',
    method: 'get',
    params
  })
}

export function getTenantById(id) {
  return request({
    url: `/tenant/${id}`,
    method: 'get'
  })
}

export function createTenant(data) {
  return request({
    url: '/tenant',
    method: 'post',
    data
  })
}

export function updateTenant(data) {
  return request({
    url: `/tenant/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteTenant(id) {
  return request({
    url: `/tenant/${id}`,
    method: 'delete'
  })
}

export function switchTenant(tenantId) {
  return request({
    url: `/tenant/switch/${tenantId}`,
    method: 'post'
  })
}
