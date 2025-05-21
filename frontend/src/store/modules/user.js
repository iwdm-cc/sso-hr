import { login, logout, getUserInfo, getUsers, getUserById, createUser, updateUser, deleteUser, assignRoles } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

const state = {
  token: getToken(),
  userInfo: {},
  tenantInfo: {},
  userList: [],
  total: 0
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  },
  SET_TENANT_INFO: (state, tenantInfo) => {
    state.tenantInfo = tenantInfo
  },
  SET_USER_LIST: (state, list) => {
    state.userList = list
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  }
}

const actions = {
  // 用户登录
  login({ commit }, loginData) {
    return new Promise((resolve, reject) => {
      login(loginData).then(response => {
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
        
        commit('SET_USER_INFO', data.user)
        commit('SET_TENANT_INFO', data.tenant)
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
        commit('SET_USER_INFO', {})
        commit('SET_TENANT_INFO', {})
        removeToken()
        router.push('/login')
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
        commit('SET_USER_LIST', data.list)
        commit('SET_TOTAL', data.total)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 根据ID获取用户信息
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

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  tenantInfo: state => state.tenantInfo,
  userList: state => state.userList,
  total: state => state.total
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}