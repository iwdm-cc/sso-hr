<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>当前租户</span>
          </div>
          <div class="card-body">
            <div class="tenant-info">
              <i class="el-icon-office-building tenant-icon"></i>
              <div class="tenant-name">{{ tenantInfo.name || '未设置' }}</div>
            </div>
            <div class="tenant-detail">
              <p><b>租户代码:</b> {{ tenantInfo.code || '-' }}</p>
              <p><b>租户状态:</b> {{ tenantInfo.status === 1 ? '启用' : '禁用' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>用户信息</span>
          </div>
          <div class="card-body">
            <div class="user-info">
              <i class="el-icon-user user-icon"></i>
              <div class="user-name">{{ userInfo.name || userInfo.username }}</div>
            </div>
            <div class="user-detail">
              <p><b>账号:</b> {{ userInfo.username || '-' }}</p>
              <p><b>状态:</b> {{ userInfo.status === 1 ? '正常' : '禁用' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>角色信息</span>
          </div>
          <div class="card-body">
            <div class="role-info">
              <i class="el-icon-s-check role-icon"></i>
              <div class="role-count">{{ userRoles.length }} 个角色</div>
            </div>
            <div class="role-list">
              <el-tag v-for="role in userRoles" :key="role.id" size="small" style="margin: 5px;">
                {{ role.name }}
              </el-tag>
              <div v-if="userRoles.length === 0" class="no-data">暂无角色信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>权限信息</span>
          </div>
          <div class="card-body">
            <div class="permission-info">
              <i class="el-icon-key permission-icon"></i>
              <div class="permission-count">{{ userPermissions.length }} 个权限</div>
            </div>
            <div class="permission-list">
              <div v-if="userPermissions.length === 0" class="no-data">暂无权限信息</div>
              <div v-else class="has-permission">您拥有系统权限</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="welcome-card" style="margin-top: 20px;">
      <div class="welcome-title">欢迎使用 SaaS 多租户用户权限管理系统</div>
      <div class="welcome-content">
        <p>本系统支持多租户架构，提供完整的用户、角色、权限管理功能。</p>
        <p>可以根据不同的租户进行隔离管理，确保数据安全和访问控制。</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      userRoles: [],
      userPermissions: []
    }
  },
  computed: {
    ...mapGetters([
      'userInfo',
      'tenantInfo'
    ])
  },
  created() {
    this.getUserData()
  },
  methods: {
    getUserData() {
      // 在实际应用中，这里应该调用API获取用户角色和权限信息
      this.userRoles = this.userInfo.roles || []
      this.userPermissions = this.userInfo.permissions || []
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 10px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.box-card {
  height: 180px;
}
.card-body {
  display: flex;
  flex-direction: column;
  height: 120px;
}
.tenant-info, .user-info, .role-info, .permission-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.tenant-icon, .user-icon, .role-icon, .permission-icon {
  font-size: 32px;
  margin-right: 10px;
  color: #409EFF;
}
.tenant-name, .user-name, .role-count, .permission-count {
  font-size: 16px;
  font-weight: bold;
}
.tenant-detail, .user-detail {
  font-size: 14px;
}
.no-data, .has-permission {
  color: #909399;
  margin-top: 10px;
  font-size: 14px;
}
.welcome-card {
  text-align: center;
  padding: 20px;
}
.welcome-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}
.welcome-content {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
}
</style>