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
  }
})