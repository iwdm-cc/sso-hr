<template>
  <div class="dashboard-container">
    <div class="welcome-info">
      <h2>欢迎使用SaaS多租户用户权限管理系统</h2>
      <p>当前租户: {{ currentTenant ? currentTenant.name : '未选择租户' }}</p>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>用户总数</span>
          </div>
          <div class="card-panel">
            <i class="el-icon-user"></i>
            <div class="card-panel-text">{{ userCount }}</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>角色总数</span>
          </div>
          <div class="card-panel">
            <i class="el-icon-s-check"></i>
            <div class="card-panel-text">{{ roleCount }}</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>权限总数</span>
          </div>
          <div class="card-panel">
            <i class="el-icon-lock"></i>
            <div class="card-panel-text">{{ permissionCount }}</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>租户总数</span>
          </div>
          <div class="card-panel">
            <i class="el-icon-office-building"></i>
            <div class="card-panel-text">{{ tenantCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>最近用户</span>
          </div>
          <el-table :data="recentUsers" style="width: 100%">
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="name" label="姓名"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="clearfix">
            <span>最近角色</span>
          </div>
          <el-table :data="recentRoles" style="width: 100%">
            <el-table-column prop="name" label="角色名称"></el-table-column>
            <el-table-column prop="description" label="描述"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      userCount: 0,
      roleCount: 0,
      permissionCount: 0,
      tenantCount: 0,
      recentUsers: [],
      recentRoles: []
    }
  },
  computed: {
    ...mapGetters([
      'currentUser',
      'currentTenant'
    ])
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      // 获取统计数据
      this.$store.dispatch('user/getUsers', { pageSize: 5 }).then(data => {
        this.userCount = data.total || 0
        this.recentUsers = data.list || []
      })
      
      this.$store.dispatch('role/getRoles', { pageSize: 5 }).then(data => {
        this.roleCount = data.total || 0
        this.recentRoles = data.list || []
      })
      
      this.$store.dispatch('permission/getPermissions').then(data => {
        this.permissionCount = data.length || 0
      })
      
      this.$store.dispatch('tenant/getTenants').then(data => {
        this.tenantCount = data.length || 0
      })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-info {
  margin-bottom: 30px;
}

.card-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
}

.card-panel i {
  font-size: 48px;
  margin-right: 15px;
  color: #409EFF;
}

.card-panel-text {
  font-size: 36px;
  font-weight: bold;
  color: #333;
}
</style>
