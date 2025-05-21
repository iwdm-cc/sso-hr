import { getPermissions, getPermissionById, createPermission, updatePermission, deletePermission } from '@/api/permission'

const state = {
  permissionList: [],
  total: 0
}

const mutations = {
  SET_PERMISSION_LIST: (state, list) => {
    state.permissionList = list
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  }
}

const actions = {
  // 获取权限列表
  getPermissions({ commit }, query) {
    return new Promise((resolve, reject) => {
      // 使用模拟数据
      setTimeout(() => {
        const mockPermissions = [
          { id: 1, name: '系统管理', code: 'system:view', type: 'menu', resourcePath: '/system', createTime: '2023-01-01 00:00:00' },
          { id: 2, name: '用户管理', code: 'user:view', type: 'menu', resourcePath: '/user', createTime: '2023-01-02 00:00:00' },
          { id: 3, name: '角色管理', code: 'role:view', type: 'menu', resourcePath: '/role', createTime: '2023-01-03 00:00:00' },
          { id: 4, name: '权限管理', code: 'permission:view', type: 'menu', resourcePath: '/permission', createTime: '2023-01-04 00:00:00' },
          { id: 5, name: '租户管理', code: 'tenant:view', type: 'menu', resourcePath: '/tenant', createTime: '2023-01-05 00:00:00' },
          { id: 6, name: '创建用户', code: 'user:create', type: 'button', resourcePath: null, createTime: '2023-01-06 00:00:00' },
          { id: 7, name: '编辑用户', code: 'user:update', type: 'button', resourcePath: null, createTime: '2023-01-07 00:00:00' },
          { id: 8, name: '删除用户', code: 'user:delete', type: 'button', resourcePath: null, createTime: '2023-01-08 00:00:00' }
        ]
        
        // 按照查询条件过滤
        let filteredPermissions = [...mockPermissions]
        if (query.name) {
          filteredPermissions = filteredPermissions.filter(perm => perm.name.includes(query.name))
        }
        if (query.code) {
          filteredPermissions = filteredPermissions.filter(perm => perm.code.includes(query.code))
        }
        if (query.type) {
          filteredPermissions = filteredPermissions.filter(perm => perm.type === query.type)
        }
        
        const mockData = {
          list: filteredPermissions,
          total: filteredPermissions.length
        }
        
        commit('SET_PERMISSION_LIST', mockData.list)
        commit('SET_TOTAL', mockData.total)
        resolve(mockData)
      }, 500)
    })
  },

  // 根据ID获取权限信息
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

const getters = {
  permissionList: state => state.permissionList,
  total: state => state.total
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}