<template>
  <div class="header">
    <div class="right-menu">
      <el-dropdown trigger="click" @command="handleTenantCommand">
        <span class="tenant-dropdown">
          {{ currentTenant ? currentTenant.name : '选择租户' }} <i class="el-icon-arrow-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item v-for="tenant in tenants" :key="tenant.id" :command="tenant.id">
            {{ tenant.name }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-dropdown">
          {{ currentUser ? currentUser.name : 'User' }} <i class="el-icon-arrow-down"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile">个人信息</el-dropdown-item>
          <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { switchTenant } from '@/api/tenant'

export default {
  name: 'Header',
  computed: {
    ...mapGetters([
      'currentUser',
      'currentTenant',
      'tenants'
    ])
  },
  created() {
    this.$store.dispatch('tenant/getTenants')
  },
  methods: {
    async handleTenantCommand(tenantId) {
      try {
        await switchTenant(tenantId)
        await this.$store.dispatch('user/getUserInfo')
        this.$message.success('切换租户成功')
      } catch (error) {
        console.error(error)
        this.$message.error('切换租户失败')
      }
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.$store.dispatch('user/logout').then(() => {
          this.$router.push('/login')
        })
      } else if (command === 'profile') {
        // 实现跳转到个人信息页面的逻辑
      }
    }
  }
}
</script>

<style scoped>
.header {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 15px;
}

.right-menu {
  display: flex;
  align-items: center;
}

.user-dropdown, .tenant-dropdown {
  color: #606266;
  cursor: pointer;
  margin-left: 15px;
}
</style>
