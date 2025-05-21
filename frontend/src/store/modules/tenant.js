import { getTenants, getTenantById, createTenant, updateTenant, deleteTenant } from '@/api/tenant'

const state = {
  tenants: []
}

const mutations = {
  SET_TENANTS: (state, tenants) => {
    state.tenants = tenants
  }
}

const actions = {
  // 获取租户列表
  getTenants({ commit }, query) {
    return new Promise((resolve, reject) => {
      getTenants(query).then(response => {
        const { data } = response
        commit('SET_TENANTS', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取单个租户
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
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
