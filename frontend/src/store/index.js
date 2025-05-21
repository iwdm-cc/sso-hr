import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import role from './modules/role'
import permission from './modules/permission'
import tenant from './modules/tenant'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    role,
    permission,
    tenant
  },
  getters: {
    token: state => state.user.token,
    currentUser: state => state.user.currentUser,
    currentTenant: state => state.user.currentTenant,
    permissions: state => state.user.permissions,
    users: state => state.user.users,
    roles: state => state.role.roles,
    permissionList: state => state.permission.permissions,
    tenants: state => state.tenant.tenants
  }
})
