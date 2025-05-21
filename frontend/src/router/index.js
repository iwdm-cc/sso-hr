import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../components/layout/Layout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘', requireAuth: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/user/Index.vue'),
        meta: { title: '用户管理', requireAuth: true },
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('../views/role/Index.vue'),
        meta: { title: '角色管理', requireAuth: true },
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('../views/permission/Index.vue'),
        meta: { title: '权限管理', requireAuth: true },
      },
      {
        path: 'tenant',
        name: 'Tenant',
        component: () => import('../views/tenant/Index.vue'),
        meta: { title: '租户管理', requireAuth: true },
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
import { getToken } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

router.beforeEach((to, from, next) => {
  NProgress.start()
  document.title = to.meta.title || 'SaaS多租户用户权限管理系统'
  
  // 简化路由守卫逻辑
  if (to.path === '/login') {
    // 已登录时访问登录页，重定向到仪表盘
    if (localStorage.getItem('token')) {
      next({ path: '/dashboard' })
    } else {
      next()
    }
  } else {
    // 非登录页面，直接放行
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router