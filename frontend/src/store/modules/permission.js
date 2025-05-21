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
      // 从后端获取真实数据
      getPermissions(query).then(response => {
        const { data } = response
        commit('SET_PERMISSION_LIST', data.list)
        commit('SET_TOTAL', data.total)
        resolve(data)
      }).catch(error => {
        console.error('获取权限列表失败:', error)
        // 处理失败时设置为空数组
        commit('SET_PERMISSION_LIST', [])
        commit('SET_TOTAL', 0)
        reject(error)
      })
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