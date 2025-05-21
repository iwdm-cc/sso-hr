import request from '@/utils/request'

// 获取租户列表
export function getTenants(params) {
  return request({
    url: '/tenant/list',
    method: 'get',
    params
  })
}

// 获取指定租户
export function getTenantById(id) {
  return request({
    url: `/tenant/${id}`,
    method: 'get'
  })
}

// 创建租户
export function createTenant(data) {
  return request({
    url: '/tenants',
    method: 'post',
    data
  })
}

// 更新租户
export function updateTenant(data) {
  return request({
    url: `/tenants/${data.id}`,
    method: 'put',
    data
  })
}

// 删除租户
export function deleteTenant(id) {
  return request({
    url: `/tenants/${id}`,
    method: 'delete'
  })
}

// 切换租户
export function switchTenant(tenantId) {
  return request({
    url: `/tenants/switch/${tenantId}`,
    method: 'post'
  })
}