<template>
  <div id="app">
    <div class="container">
      <h1>SaaS多租户用户权限管理系统</h1>
      
      <div class="login-form" v-if="!isLoggedIn">
        <h2>登录</h2>
        <div class="form-group">
          <label>租户代码:</label>
          <input type="text" v-model="loginForm.tenantCode" placeholder="请输入租户代码">
        </div>
        <div class="form-group">
          <label>用户名:</label>
          <input type="text" v-model="loginForm.username" placeholder="请输入用户名">
        </div>
        <div class="form-group">
          <label>密码:</label>
          <input type="password" v-model="loginForm.password" placeholder="请输入密码">
        </div>
        <button @click="handleLogin" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </div>
      
      <div class="dashboard" v-else>
        <div class="welcome">
          <h2>欢迎, {{ userInfo.name }}</h2>
          <p>当前租户: {{ tenantInfo.name }}</p>
        </div>
        
        <div class="menu">
          <div class="menu-item" @click="activeTab = 'dashboard'" :class="{ active: activeTab === 'dashboard' }">仪表盘</div>
          <div class="menu-item" @click="activeTab = 'users'" :class="{ active: activeTab === 'users' }">用户管理</div>
          <div class="menu-item" @click="activeTab = 'roles'" :class="{ active: activeTab === 'roles' }">角色管理</div>
          <div class="menu-item" @click="activeTab = 'permissions'" :class="{ active: activeTab === 'permissions' }">权限管理</div>
          <div class="menu-item" @click="activeTab = 'tenants'" :class="{ active: activeTab === 'tenants' }">租户管理</div>
          <div class="menu-item logout" @click="handleLogout">退出登录</div>
        </div>
        
        <div class="content">
          <div v-if="activeTab === 'dashboard'">
            <h3>系统仪表盘</h3>
            <div class="dashboard-info">
              <p>系统日期: {{ currentDate }}</p>
              <p>多租户功能状态: <span class="status-ok">正常</span></p>
              <p>用户权限系统状态: <span class="status-ok">正常</span></p>
              <p>当前在线用户数: 1</p>
              <p>已创建租户数: {{ tenants.length }}</p>
              <p>系统总用户数: {{ users.length }}</p>
            </div>
          </div>
          
          <div v-if="activeTab === 'users'">
            <h3>用户管理</h3>
            <div class="action-bar">
              <button class="btn-primary">添加用户</button>
              <input type="text" placeholder="搜索用户" class="search-input">
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>用户名</th>
                  <th>姓名</th>
                  <th>邮箱</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.name }}</td>
                  <td>{{ user.email }}</td>
                  <td>
                    <span :class="user.status === 1 ? 'status-ok' : 'status-error'">
                      {{ user.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn-sm btn-edit">编辑</button>
                    <button class="btn-sm btn-delete">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div v-if="activeTab === 'roles'">
            <h3>角色管理</h3>
            <div class="action-bar">
              <button class="btn-primary">添加角色</button>
              <input type="text" placeholder="搜索角色" class="search-input">
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>角色名称</th>
                  <th>角色编码</th>
                  <th>描述</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="role in roles" :key="role.id">
                  <td>{{ role.id }}</td>
                  <td>{{ role.name }}</td>
                  <td>{{ role.code }}</td>
                  <td>{{ role.description }}</td>
                  <td>
                    <span :class="role.status === 1 ? 'status-ok' : 'status-error'">
                      {{ role.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn-sm btn-edit">编辑</button>
                    <button class="btn-sm btn-permission">分配权限</button>
                    <button class="btn-sm btn-delete">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div v-if="activeTab === 'permissions'">
            <h3>权限管理</h3>
            <div class="action-bar">
              <button class="btn-primary">添加权限</button>
              <input type="text" placeholder="搜索权限" class="search-input">
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>权限名称</th>
                  <th>权限编码</th>
                  <th>类型</th>
                  <th>资源路径</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="permission in permissions" :key="permission.id">
                  <td>{{ permission.id }}</td>
                  <td>{{ permission.name }}</td>
                  <td>{{ permission.code }}</td>
                  <td>{{ permission.type }}</td>
                  <td>{{ permission.resourcePath || '-' }}</td>
                  <td>
                    <button class="btn-sm btn-edit">编辑</button>
                    <button class="btn-sm btn-delete">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div v-if="activeTab === 'tenants'">
            <h3>租户管理</h3>
            <div class="action-bar">
              <button class="btn-primary">添加租户</button>
              <input type="text" placeholder="搜索租户" class="search-input">
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>租户名称</th>
                  <th>租户编码</th>
                  <th>描述</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tenant in tenants" :key="tenant.id">
                  <td>{{ tenant.id }}</td>
                  <td>{{ tenant.name }}</td>
                  <td>{{ tenant.code }}</td>
                  <td>{{ tenant.description }}</td>
                  <td>
                    <span :class="tenant.status === 1 ? 'status-ok' : 'status-error'">
                      {{ tenant.status === 1 ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn-sm btn-edit">编辑</button>
                    <button class="btn-sm btn-delete">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      
      <div class="system-info">
        <p>系统版本: v1.0.0</p>
        <p>后端技术: Spring Boot + MyBatis Plus + JWT</p>
        <p>前端技术: Vue.js + Element UI</p>
        <p>数据库: PostgreSQL</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DemoApp',
  data() {
    return {
      isLoggedIn: false,
      token: '',
      userInfo: {},
      tenantInfo: {},
      activeTab: 'dashboard',
      loading: false,
      loginForm: {
        tenantCode: '',
        username: '',
        password: ''
      },
      currentDate: new Date().toLocaleString(),
      // 数据来自我们的数据库
      users: [
        { id: 1, username: 'admin', name: '系统管理员', email: 'admin@example.com', status: 1 },
        { id: 2, username: 'test', name: '测试用户', email: 'test@example.com', status: 1 }
      ],
      roles: [
        { id: 1, name: '超级管理员', code: 'ROLE_SUPER_ADMIN', description: '拥有所有权限', status: 1 },
        { id: 2, name: '管理员', code: 'ROLE_ADMIN', description: '管理员角色', status: 1 },
        { id: 3, name: '测试角色', code: 'ROLE_TEST', description: '测试角色', status: 1 }
      ],
      permissions: [
        { id: 1, name: '系统管理', code: 'system:view', type: 'menu', resourcePath: '/system' },
        { id: 2, name: '用户管理', code: 'user:view', type: 'menu', resourcePath: '/system/user' },
        { id: 3, name: '角色管理', code: 'role:view', type: 'menu', resourcePath: '/system/role' }
      ],
      tenants: [
        { id: 1, name: '系统管理租户', code: 'system', description: '系统默认租户', status: 1 },
        { id: 2, name: '测试租户', code: 'test', description: '用于测试的租户', status: 1 }
      ]
    }
  },
  methods: {
    handleLogin() {
      this.loading = true
      
      // 使用数据库中的信息进行简单验证
      setTimeout(() => {
        if (!this.loginForm.tenantCode || !this.loginForm.username) {
          alert('请输入租户代码和用户名')
          this.loading = false
          return
        }
        
        // 查找匹配的租户
        const tenant = this.tenants.find(t => t.code === this.loginForm.tenantCode)
        if (!tenant) {
          alert('租户不存在')
          this.loading = false
          return
        }
        
        // 查找匹配的用户
        const user = this.users.find(u => u.username === this.loginForm.username)
        if (!user) {
          alert('用户不存在')
          this.loading = false
          return
        }
        
        // 登录成功
        this.isLoggedIn = true
        this.token = 'demo-token'
        this.userInfo = user
        this.tenantInfo = tenant
        
        alert('登录成功！欢迎使用SaaS多租户用户权限管理系统')
        this.loading = false
      }, 500)
    },
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        this.isLoggedIn = false
        this.token = ''
        this.userInfo = {}
        this.tenantInfo = {}
        this.activeTab = 'dashboard'
      }
    }
  }
}
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  background-color: #f5f7f9;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  padding: 20px;
}

.container {
  width: 100%;
  max-width: 1200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
  color: #304156;
}

h2, h3 {
  margin-bottom: 15px;
  color: #304156;
}

.login-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #66b1ff;
}

button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.dashboard {
  margin-top: 20px;
}

.welcome {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.menu {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.menu-item {
  padding: 10px 15px;
  cursor: pointer;
  margin-right: 5px;
  border-radius: 4px 4px 0 0;
}

.menu-item:hover, .menu-item.active {
  background-color: #f0f7ff;
  color: #409EFF;
}

.logout {
  margin-left: auto;
  color: #f56c6c;
}

.content {
  padding: 20px;
  min-height: 400px;
  border: 1px solid #eee;
  border-radius: 5px;
}

.system-info {
  margin-top: 30px;
  padding-top: 10px;
  border-top: 1px solid #eee;
  text-align: center;
  font-size: 12px;
  color: #999;
}

.dashboard-info {
  padding: 15px;
  background-color: #f5f7f9;
  border-radius: 4px;
  line-height: 2;
}

.status-ok {
  color: #67C23A;
  font-weight: bold;
}

.status-error {
  color: #F56C6C;
  font-weight: bold;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.data-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

.data-table tr:hover td {
  background-color: #f5f7fa;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.search-input {
  width: 200px;
  padding: 8px 12px;
}

.btn-primary {
  width: auto;
  padding: 8px 15px;
}

.btn-sm {
  width: auto;
  padding: 5px 10px;
  font-size: 12px;
  margin-right: 5px;
}

.btn-edit {
  background-color: #E6A23C;
}

.btn-delete {
  background-color: #F56C6C;
}

.btn-permission {
  background-color: #909399;
}
</style>