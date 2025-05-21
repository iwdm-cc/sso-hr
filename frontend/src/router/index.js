import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../components/layout/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页', icon: 'el-icon-s-home' }
      },
      {
        path: 'user',
        name: 'User',
        component: { render: h => h('router-view') },
        redirect: '/user/list',
        meta: { title: '用户管理', icon: 'el-icon-user' },
        children: [
          {
            path: 'list',
            name: 'UserList',
            component: () => import('../views/user/List.vue'),
            meta: { title: '用户列表' }
          },
          {
            path: 'edit/:id?',
            name: 'UserEdit',
            component: () => import('../views/user/Edit.vue'),
            meta: { title: '编辑用户', hidden: true }
          }
        ]
      },
      {
        path: 'role',
        name: 'Role',
        component: { render: h => h('router-view') },
        redirect: '/role/list',
        meta: { title: '角色管理', icon: 'el-icon-s-check' },
        children: [
          {
            path: 'list',
            name: 'RoleList',
            component: () => import('../views/role/List.vue'),
            meta: { title: '角色列表' }
          },
          {
            path: 'edit/:id?',
            name: 'RoleEdit',
            component: () => import('../views/role/Edit.vue'),
            meta: { title: '编辑角色', hidden: true }
          }
        ]
      },
      {
        path: 'permission',
        name: 'Permission',
        component: { render: h => h('router-view') },
        redirect: '/permission/list',
        meta: { title: '权限管理', icon: 'el-icon-lock' },
        children: [
          {
            path: 'list',
            name: 'PermissionList',
            component: () => import('../views/permission/List.vue'),
            meta: { title: '权限列表' }
          },
          {
            path: 'edit/:id?',
            name: 'PermissionEdit',
            component: () => import('../views/permission/Edit.vue'),
            meta: { title: '编辑权限', hidden: true }
          }
        ]
      },
      {
        path: 'tenant',
        name: 'Tenant',
        component: { render: h => h('router-view') },
        redirect: '/tenant/list',
        meta: { title: '租户管理', icon: 'el-icon-office-building' },
        children: [
          {
            path: 'list',
            name: 'TenantList',
            component: () => import('../views/tenant/List.vue'),
            meta: { title: '租户列表' }
          },
          {
            path: 'edit/:id?',
            name: 'TenantEdit',
            component: () => import('../views/tenant/Edit.vue'),
            meta: { title: '编辑租户', hidden: true }
          }
        ]
      }
    ]
  },
  {
    path: '*',
    redirect: '/404'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
