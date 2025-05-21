import { getRoles, getRoleById, createRole, updateRole, deleteRole, assignPermissions, getRolePermissions } from '@/api/role'

const state = {
  roleList: [],
  total: 0
}

const mutations = {
  SET_ROLE_LIST: (state, list) => {
    state.roleList = list
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  }
}

const actions = {
  // 获取角色列表
  getRoles({ commit }, query) {
    return new Promise((resolve, reject) => {
      // 使用模拟数据
      setTimeout(() => {
        const mockRoles = [
          { id: 1, name: '超级管理员', code: 'ROLE_SUPER_ADMIN', description: '拥有所有权限', status: 1, createTime: '2023-01-01 00:00:00' },
          { id: 2, name: '系统管理员', code: 'ROLE_ADMIN', description: '管理系统基本功能', status: 1, createTime: '2023-01-02 00:00:00' },
          { id: 3, name: '用户管理员', code: 'ROLE_USER_ADMIN', description: '管理用户相关功能', status: 1, createTime: '2023-01-03 00:00:00' },
          { id: 4, name: '普通用户', code: 'ROLE_USER', description: '普通用户权限', status: 1, createTime: '2023-01-04 00:00:00' },
          { id: 5, name: '访客', code: 'ROLE_GUEST', description: '只有查看权限', status: 0, createTime: '2023-01-05 00:00:00' }
        ]
        
        // 按照查询条件过滤
        let filteredRoles = [...mockRoles]
        if (query.name) {
          filteredRoles = filteredRoles.filter(role => role.name.includes(query.name))
        }
        if (query.status !== '' && query.status !== undefined) {
          filteredRoles = filteredRoles.filter(role => role.status === parseInt(query.status))
        }
        
        const mockData = {
          list: filteredRoles,
          total: filteredRoles.length
        }
        
        commit('SET_ROLE_LIST', mockData.list)
        commit('SET_TOTAL', mockData.total)
        resolve(mockData)
      }, 500)
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
  roleList: state => state.roleList,
  total: state => state.total
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}