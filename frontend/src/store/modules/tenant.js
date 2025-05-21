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
      // 从后端获取真实数据
      getTenants(query).then(response => {
        const { data } = response
        commit('SET_TENANT_LIST', data.list)
        commit('SET_TOTAL', data.total)
        resolve(data)
      }).catch(error => {
        console.error('获取租户列表失败:', error)
        // 处理失败时设置为空数组
        commit('SET_TENANT_LIST', [])
        commit('SET_TOTAL', 0)
        reject(error)
      })
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