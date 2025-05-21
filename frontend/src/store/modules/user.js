import { login, logout, getUserInfo, getUsers, getUserById, createUser, updateUser, deleteUser, assignRoles } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken(),
  currentUser: null,
  currentTenant: null,
  permissions: [],
  users: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_CURRENT_USER: (state, user) => {
    state.currentUser = user
  },
  SET_CURRENT_TENANT: (state, tenant) => {
    state.currentTenant = tenant
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  },
  SET_USERS: (state, users) => {
    state.users = users
  }
}

const actions = {
  // 登录
  login({ commit }, loginInfo) {
    return new Promise((resolve, reject) => {
      login(loginInfo).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取用户信息
  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo().then(response => {
        const { data } = response
        if (!data) {
          reject('验证失败，请重新登录')
        }
        
        const { user, tenant, permissions } = data
        commit('SET_CURRENT_USER', user)
        commit('SET_CURRENT_TENANT', tenant)
        commit('SET_PERMISSIONS', permissions)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 退出登录
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      logout().then(() => {
        commit('SET_TOKEN', '')
        commit('SET_CURRENT_USER', null)
        commit('SET_CURRENT_TENANT', null)
        commit('SET_PERMISSIONS', [])
        removeToken()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取用户列表
  getUsers({ commit }, query) {
    return new Promise((resolve, reject) => {
      getUsers(query).then(response => {
        const { data } = response
        commit('SET_USERS', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取单个用户
  getUserById({ commit }, id) {
    return new Promise((resolve, reject) => {
      getUserById(id).then(response => {
        const { data } = response
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 创建用户
  createUser({ commit }, user) {
    return new Promise((resolve, reject) => {
      createUser(user).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 更新用户
  updateUser({ commit }, user) {
    return new Promise((resolve, reject) => {
      updateUser(user).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 删除用户
  deleteUser({ commit }, id) {
    return new Promise((resolve, reject) => {
      deleteUser(id).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 分配角色
  assignRoles({ commit }, { userId, roleIds }) {
    return new Promise((resolve, reject) => {
      assignRoles(userId, roleIds).then(response => {
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
