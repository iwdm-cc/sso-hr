import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import role from './modules/role'
import permission from './modules/permission'
import tenant from './modules/tenant'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user,
    role,
    permission,
    tenant
  },
  getters: {
    // 添加全局getters，解决找不到的问题
    userInfo: state => state.user.userInfo || { 
      name: '测试用户',
      username: 'test',
      permissions: ['system:view', 'user:view', 'role:view']
    },
    tenantInfo: state => state.user.tenantInfo || { 
      name: '系统租户',
      code: 'system'
    }
  }
})