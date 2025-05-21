<template>
  <div class="navbar">
    <div class="title">
      SaaS多租户用户权限管理系统
    </div>
    <div class="right-menu">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="el-dropdown-link">
          {{ userInfo.name || '用户' }}<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="switchTenant" v-if="isAdmin">切换租户</el-dropdown-item>
          <el-dropdown-item command="userInfo">个人信息</el-dropdown-item>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    
    <!-- 切换租户对话框 -->
    <el-dialog title="切换租户" :visible.sync="tenantDialogVisible" width="30%">
      <el-select v-model="selectedTenantId" placeholder="请选择租户" style="width: 100%">
        <el-option
          v-for="item in tenantList"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="tenantDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmSwitchTenant">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Header',
  data() {
    return {
      tenantDialogVisible: false,
      tenantList: [],
      selectedTenantId: null
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    isAdmin() {
      // 简化管理员判断逻辑
      return true
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确认退出系统?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
          })
        }).catch(() => {})
      } else if (command === 'switchTenant') {
        this.getTenantList()
        this.tenantDialogVisible = true
      } else if (command === 'userInfo') {
        this.$message.info('功能开发中...')
      }
    },
    getTenantList() {
      this.$store.dispatch('tenant/getTenants', { page: 1, pageSize: 100 }).then(res => {
        this.tenantList = res.list || []
        if (this.tenantList.length > 0 && !this.selectedTenantId) {
          this.selectedTenantId = this.tenantList[0].id
        }
      })
    },
    confirmSwitchTenant() {
      if (!this.selectedTenantId) {
        this.$message.warning('请选择租户')
        return
      }
      
      this.$store.dispatch('tenant/switchTenant', this.selectedTenantId).then(() => {
        this.tenantDialogVisible = false
        this.$message.success('切换租户成功')
        // 重新获取用户信息
        this.$store.dispatch('user/getUserInfo')
      }).catch(error => {
        console.error(error)
      })
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.title {
  font-size: 18px;
  font-weight: bold;
  color: #304156;
}
.right-menu {
  display: flex;
  align-items: center;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style>