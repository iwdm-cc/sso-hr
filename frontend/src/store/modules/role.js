import { getRoles, getRoleById, createRole, updateRole, deleteRole, assignPermissions, getRolePermissions } from '@/api/role'

const state = {
  roles: [],
  total: 0
}

const mutations = {
  SET_ROLE_LIST: (state, list) => {
    state.roles = list
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  }
}

const actions = {
  // 获取角色列表
  getRoles({ commit }, query) {
    return new Promise((resolve, reject) => {
      // 从后端数据库获取真实数据
      getRoles(query).then(response => {
        const { data } = response
        console.log("角色列表响应:", response)
        commit('SET_ROLE_LIST', data.list || [])
        commit('SET_TOTAL', data.total || 0)
        resolve(data)
      }).catch(error => {
        console.error('获取角色列表失败:', error)
        // 出错时使用空数组
        commit('SET_ROLE_LIST', [])
        commit('SET_TOTAL', 0)
        reject(error)
      })
    })
  },

  // 根据ID获取角色信息
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
  },

  // 获取角色权限
  getRolePermissions({ commit }, roleId) {
    return new Promise((resolve, reject) => {
      getRolePermissions(roleId).then(response => {
        const { data } = response
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

const getters = {
  roleList: state => state.roles,
  total: state => state.total
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}