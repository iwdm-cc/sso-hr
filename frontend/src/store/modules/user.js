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
        
        // 直接从登录响应中获取用户和租户信息
        const userInfo = {
          id: data.userId,
          username: data.username,
          name: data.name,
          avatar: data.avatar
        }
        
        const tenantInfo = {
          id: data.tenantId,
          name: data.tenantName
        }
        
        commit('SET_USER_INFO', userInfo)
        commit('SET_TENANT_INFO', tenantInfo)
        
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
      // 使用静态数据进行演示
      setTimeout(() => {
        const mockUsers = [
          { id: 1, username: 'admin', name: '系统管理员', email: 'admin@example.com', phone: '13800000000', status: 1, createTime: '2023-01-01 00:00:00' },
          { id: 2, username: 'test', name: '测试用户', email: 'test@example.com', phone: '13900000000', status: 1, createTime: '2023-01-02 00:00:00' },
          { id: 3, username: 'user1', name: '普通用户1', email: 'user1@example.com', phone: '13700000000', status: 1, createTime: '2023-01-03 00:00:00' },
          { id: 4, username: 'user2', name: '普通用户2', email: 'user2@example.com', phone: '13600000000', status: 0, createTime: '2023-01-04 00:00:00' }
        ]
        
        // 按照查询条件过滤
        let filteredUsers = [...mockUsers]
        if (query.username) {
          filteredUsers = filteredUsers.filter(user => user.username.includes(query.username))
        }
        if (query.name) {
          filteredUsers = filteredUsers.filter(user => user.name.includes(query.name))
        }
        if (query.status !== '' && query.status !== undefined) {
          filteredUsers = filteredUsers.filter(user => user.status === parseInt(query.status))
        }
        
        const mockData = {
          list: filteredUsers,
          total: filteredUsers.length
        }
        
        commit('SET_USER_LIST', mockData.list)
        commit('SET_TOTAL', mockData.total)
        resolve(mockData)
      }, 500)
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
      // 使用模拟数据处理用户创建
      setTimeout(() => {
        // 模拟成功响应
        const mockResponse = {
          code: 200,
          message: '用户创建成功',
          data: {
            ...user,
            id: Math.floor(Math.random() * 1000) + 10, // 生成随机ID
            createTime: new Date().toISOString().split('T')[0] + ' ' + new Date().toTimeString().split(' ')[0]
          }
        };
        
        // 添加到用户列表
        const newUser = mockResponse.data;
        commit('SET_USER_LIST', [...state.userList, newUser]);
        
        resolve(mockResponse);
      }, 500);
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