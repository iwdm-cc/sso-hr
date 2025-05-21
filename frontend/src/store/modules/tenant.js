import { getTenants, getTenantById, createTenant, updateTenant, deleteTenant, switchTenant } from '@/api/tenant'

const state = {
  tenantList: [],
  total: 0
}

const mutations = {
  SET_TENANT_LIST: (state, list) => {
    state.tenantList = list
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  }
}

const actions = {
  // 获取租户列表
  getTenants({ commit }, query) {
    return new Promise((resolve, reject) => {
      // 使用模拟数据
      setTimeout(() => {
        const mockTenants = [
          { id: 1, name: '系统管理租户', code: 'system', description: '系统默认租户', status: 1, createTime: '2023-01-01 00:00:00' },
          { id: 2, name: '测试租户', code: 'test', description: '用于测试的租户', status: 1, createTime: '2023-01-02 00:00:00' },
          { id: 3, name: '开发租户', code: 'dev', description: '开发使用的租户', status: 1, createTime: '2023-01-03 00:00:00' },
          { id: 4, name: '生产租户', code: 'prod', description: '生产环境租户', status: 1, createTime: '2023-01-04 00:00:00' },
          { id: 5, name: '禁用租户', code: 'disabled', description: '已禁用的租户', status: 0, createTime: '2023-01-05 00:00:00' }
        ]
        
        // 按照查询条件过滤
        let filteredTenants = [...mockTenants]
        if (query.name) {
          filteredTenants = filteredTenants.filter(tenant => tenant.name.includes(query.name))
        }
        if (query.status !== '' && query.status !== undefined) {
          filteredTenants = filteredTenants.filter(tenant => tenant.status === parseInt(query.status))
        }
        
        const mockData = {
          list: filteredTenants,
          total: filteredTenants.length
        }
        
        commit('SET_TENANT_LIST', mockData.list)
        commit('SET_TOTAL', mockData.total)
        resolve(mockData)
      }, 500)
    })
  },

  // 根据ID获取租户信息
  getTenantById({ commit }, id) {
    return new Promise((resolve, reject) => {
      getTenantById(id).then(response => {
        const { data } = response
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 创建租户
  createTenant({ commit }, tenant) {
    return new Promise((resolve, reject) => {
      createTenant(tenant).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 更新租户
  updateTenant({ commit }, tenant) {
    return new Promise((resolve, reject) => {
      updateTenant(tenant).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 删除租户
  deleteTenant({ commit }, id) {
    return new Promise((resolve, reject) => {
      deleteTenant(id).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 切换租户
  switchTenant({ commit }, tenantId) {
    return new Promise((resolve, reject) => {
      switchTenant(tenantId).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

const getters = {
  tenantList: state => state.tenantList,
  total: state => state.total
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}