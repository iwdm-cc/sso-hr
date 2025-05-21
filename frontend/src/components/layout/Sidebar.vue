<template>
  <div>
    <el-menu
      :default-active="activeMenu"
      :collapse="false"
      :background-color="variables.menuBg"
      :text-color="variables.menuText"
      :active-text-color="variables.menuActiveText"
      router
    >
      <el-menu-item index="/dashboard">
        <i class="el-icon-s-home"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      
      <el-submenu index="/user">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/user/list">用户列表</el-menu-item>
      </el-submenu>
      
      <el-submenu index="/role">
        <template slot="title">
          <i class="el-icon-s-check"></i>
          <span>角色管理</span>
        </template>
        <el-menu-item index="/role/list">角色列表</el-menu-item>
      </el-submenu>
      
      <el-submenu index="/permission">
        <template slot="title">
          <i class="el-icon-lock"></i>
          <span>权限管理</span>
        </template>
        <el-menu-item index="/permission/list">权限列表</el-menu-item>
      </el-submenu>
      
      <el-submenu index="/tenant">
        <template slot="title">
          <i class="el-icon-office-building"></i>
          <span>租户管理</span>
        </template>
        <el-menu-item index="/tenant/list">租户列表</el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
  computed: {
    activeMenu() {
      const { meta, path } = this.$route
      // 如果设置了高亮路径，则使用高亮路径
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    variables() {
      return {
        menuBg: '#304156',
        menuText: '#bfcbd9',
        menuActiveText: '#409EFF'
      }
    }
  }
}
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>
