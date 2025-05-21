<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <sidebar />
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <app-header />
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
      <el-footer class="layout-footer">
        © 2023 SaaS多租户用户权限管理系统
      </el-footer>
    </el-container>
  </el-container>
</template>

<script>
import Sidebar from './Sidebar.vue';
import AppHeader from './Header.vue';

export default {
  name: 'Layout',
  components: {
    Sidebar,
    AppHeader
  }
};
</script>

<style scoped>
.layout-container {
  height: 100%;
}

.layout-aside {
  background-color: #304156;
  overflow: hidden;
  transition: width 0.3s;
}

.layout-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  padding: 0;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
}

.layout-main {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 120px);
}

.layout-footer {
  text-align: center;
  color: #666;
  font-size: 12px;
  line-height: 60px;
  padding: 0;
  background-color: #f0f2f5;
}
</style>
