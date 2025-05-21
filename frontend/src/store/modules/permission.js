import { getPermissions, getPermissionById, createPermission, updatePermission, deletePermission } from '@/api/permission'

const state = {
  permissions: []
}

const mutations = {
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  }
}

const actions = {
  // 获取权限列表
  getPermissions({ commit }, query) {
    return new Promise((resolve, reject) => {
      getPermissions(query).then(response => {
        const { data } = response
        commit('SET_PERMISSIONS', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取单个权限
  getPermissionById({ commit }, id) {
    return new Promise((resolve, reject) => {
      getPermissionById(id).then(response => {
        const { data } = response
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 创建权限
  createPermission({ commit }, permission) {
    return new Promise((resolve, reject) => {
      createPermission(permission).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 更新权限
  updatePermission({ commit }, permission) {
    return new Promise((resolve, reject) => {
      updatePermission(permission).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 删除权限
  deletePermission({ commit }, id) {
    return new Promise((resolve, reject) => {
      deletePermission(id).then(response => {
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
