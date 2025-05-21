<template>
  <div class="login-container">
    <el-form class="login-form" ref="loginForm" :model="loginForm" :rules="loginRules">
      <div class="title-container">
        <h3 class="title">SaaS多租户用户权限管理系统</h3>
      </div>

      <el-form-item prop="username">
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="用户名"
          prefix-icon="el-icon-user"
          name="username"
          type="text"
          autocomplete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          ref="password"
          v-model="loginForm.password"
          placeholder="密码"
          prefix-icon="el-icon-lock"
          name="password"
          type="password"
          autocomplete="on"
          @keyup.enter.native="handleLogin"
        />
      </el-form-item>

      <el-form-item prop="tenantId">
        <el-select v-model="loginForm.tenantId" placeholder="请选择租户" style="width: 100%">
          <el-option
            v-for="item in tenants"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width: 100%; margin-bottom: 30px" @click.native.prevent="handleLogin">
        登录
      </el-button>
    </el-form>
  </div>
</template>

<script>
import { getTenants } from '@/api/tenant'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        tenantId: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        tenantId: [{ required: true, message: '请选择租户', trigger: 'change' }]
      },
      loading: false,
      tenants: [],
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getTenants()
  },
  methods: {
    getTenants() {
      getTenants().then(response => {
        this.tenants = response.data
      }).catch(error => {
        console.error('获取租户列表失败:', error)
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || '/' })
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          console.log('表单校验失败')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100%;
  width: 100%;
  background-color: #2d3a4b;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  width: 420px;
  max-width: 100%;
  background: #fff;
  padding: 35px;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title-container {
  margin-bottom: 30px;
  text-align: center;
}

.title {
  font-size: 22px;
  color: #303133;
  margin: 0;
}
</style>
