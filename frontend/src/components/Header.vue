<template>
  <div class="header-container">
    <div class="left-menu">
      <span class="current-route">{{ currentRouteName }}</span>
    </div>
    <div class="right-menu">
      <!-- 全屏按钮 -->
      <el-tooltip content="全屏" effect="dark" placement="bottom">
        <div class="right-menu-item" @click="toggleFullScreen">
          <i class="el-icon-full-screen"></i>
        </div>
      </el-tooltip>
      
      <!-- 用户信息下拉菜单 -->
      <el-dropdown class="right-menu-item user-dropdown" trigger="click">
        <div class="user-info">
          <span>{{ userInfo.username || '用户' }}</span>
          <i class="el-icon-arrow-down"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>
            <div @click="showUserInfo">用户资料</div>
          </el-dropdown-item>
          <el-dropdown-item divided>
            <div @click="handleLogout">退出登录</div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'AppHeader',
  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    }),
    // 当前路由名称
    currentRouteName() {
      return this.$route.meta.title || this.$route.name;
    }
  },
  methods: {
    // 切换全屏
    toggleFullScreen() {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen();
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        }
      }
    },
    
    // 显示用户信息
    showUserInfo() {
      this.$message({
        message: '功能开发中',
        type: 'info'
      });
    },
    
    // 处理登出
    handleLogout() {
      this.$confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/logout').then(() => {
          this.$router.push('/login');
        });
      }).catch(() => {
        // 取消登出
      });
    }
  }
};
</script>

<style scoped>
.header-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
}

.left-menu {
  display: flex;
  align-items: center;
}

.current-route {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.right-menu {
  display: flex;
  align-items: center;
}

.right-menu-item {
  padding: 0 10px;
  cursor: pointer;
  font-size: 18px;
  color: #5a5e66;
  vertical-align: middle;
}

.user-dropdown {
  height: 100%;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-right: 5px;
}
</style>
