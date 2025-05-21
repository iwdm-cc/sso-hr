<template>
  <div class="sidebar">
    <el-menu
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      :default-active="$route.path"
      router
      unique-opened
    >
      <el-menu-item index="/dashboard">
        <i class="el-icon-s-home"></i>
        <span slot="title">仪表盘</span>
      </el-menu-item>
      
      <el-menu-item index="/user">
        <i class="el-icon-user"></i>
        <span slot="title">用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="/role">
        <i class="el-icon-s-check"></i>
        <span slot="title">角色管理</span>
      </el-menu-item>
      
      <el-menu-item index="/permission">
        <i class="el-icon-key"></i>
        <span slot="title">权限管理</span>
      </el-menu-item>
      
      <el-menu-item index="/tenant">
        <i class="el-icon-office-building"></i>
        <span slot="title">租户管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar'
}
</script>

<style scoped>
.sidebar {
  height: 100%;
}
.el-menu {
  border-right: none;
  height: 100%;
}
</style>