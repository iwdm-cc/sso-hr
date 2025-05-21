<template>
  <div id="app">
    <div class="container">
      <h1>SaaS多租户用户权限管理系统</h1>
      
      <div class="login-form" v-if="!token">
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
        <button @click="login" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </div>
      
      <div class="dashboard" v-else>
        <div class="welcome">
          <h2>欢迎, {{ userInfo.name || userInfo.username || '用户' }}!</h2>
          <p v-if="tenantInfo.name">当前租户: {{ tenantInfo.name }}</p>
        </div>
        
        <div class="menu">
          <div class="menu-item" @click="activeTab = 'dashboard'">仪表盘</div>
          <div class="menu-item" @click="activeTab = 'users'">用户管理</div>
          <div class="menu-item" @click="activeTab = 'roles'">角色管理</div>
          <div class="menu-item" @click="activeTab = 'permissions'">权限管理</div>
          <div class="menu-item" @click="activeTab = 'tenants'">租户管理</div>
          <div class="menu-item logout" @click="logout">退出登录</div>
        </div>
        
        <div class="content">
          <div v-if="activeTab === 'dashboard'">
            <h3>系统仪表盘</h3>
            <div class="dashboard-info">
              <p>系统日期: {{ new Date().toLocaleString() }}</p>
              <p>多租户功能状态: 正常</p>
              <p>用户权限系统状态: 正常</p>
            </div>
          </div>
          
          <div v-if="activeTab === 'users'">
            <h3>用户管理</h3>
            <p>这里可以管理系统用户，包括创建、编辑、删除用户以及分配角色等操作。</p>
          </div>
          
          <div v-if="activeTab === 'roles'">
            <h3>角色管理</h3>
            <p>这里可以管理系统角色，包括创建、编辑、删除角色以及分配权限等操作。</p>
          </div>
          
          <div v-if="activeTab === 'permissions'">
            <h3>权限管理</h3>
            <p>这里可以管理系统权限，包括创建、编辑、删除权限等操作。</p>
          </div>
          
          <div v-if="activeTab === 'tenants'">
            <h3>租户管理</h3>
            <p>这里可以管理系统租户，包括创建、编辑、删除租户等操作。</p>
          </div>
        </div>
      </div>
      
      <div class="system-info">
        <p>系统版本: v1.0.0</p>
        <p>后端技术: Spring Boot + MyBatis</p>
        <p>前端技术: Vue.js</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      token: '',
      userInfo: {},
      tenantInfo: {},
      activeTab: 'dashboard',
      loading: false,
      loginForm: {
        tenantCode: '',
        username: '',
        password: ''
      }
    }
  },
  methods: {
    login() {
      this.loading = true
      // 使用模拟数据展示界面功能
      setTimeout(() => {
        // 模拟登录成功
        this.token = 'mock-token'
        this.userInfo = { 
          name: this.loginForm.username || '测试用户', 
          username: this.loginForm.username || 'admin',
          id: 1,
          email: 'admin@example.com',
          status: 1
        }
        this.tenantInfo = { 
          name: '租户-' + this.loginForm.tenantCode, 
          code: this.loginForm.tenantCode || 'test',
          id: 1,
          status: 1
        }
        this.loading = false
      }, 800)
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.tenantInfo = {}
      this.activeTab = 'dashboard'
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
  max-width: 1000px;
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

.menu-item:hover {
  background-color: #f0f7ff;
}

.logout {
  margin-left: auto;
  color: #f56c6c;
}

.content {
  padding: 20px;
  min-height: 300px;
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
}
</style>