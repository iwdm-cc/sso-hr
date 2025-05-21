import { getRoles, getRoleById, createRole, updateRole, deleteRole, assignPermissions } from '@/api/role'

const state = {
  roles: []
}

const mutations = {
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // 获取角色列表
  getRoles({ commit }, query) {
    return new Promise((resolve, reject) => {
      getRoles(query).then(response => {
        const { data } = response
        commit('SET_ROLES', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取单个角色
  getRoleById({ commit }, id) {
    return new Promise((resolve, reject) => {
      getRoleById(id).then(response => {
        const { data } = response
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 创建角色
  createRole({ commit }, role) {
    return new Promise((resolve, reject) => {
      createRole(role).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 更新角色
  updateRole({ commit }, role) {
    return new Promise((resolve, reject) => {
      updateRole(role).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 删除角色
  deleteRole({ commit }, id) {
    return new Promise((resolve, reject) => {
      deleteRole(id).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 分配权限
  assignPermissions({ commit }, { roleId, permissionIds }) {
    return new Promise((resolve, reject) => {
      assignPermissions(roleId, permissionIds).then(response => {
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
