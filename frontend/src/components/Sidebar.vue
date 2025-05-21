<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="isCollapse"
      :unique-opened="true"
      :collapse-transition="false"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router>
      
      <div class="logo-container">
        <div class="logo-title" v-if="!isCollapse">SaaS权限中心</div>
        <div class="logo-icon" v-else>SaaS</div>
      </div>
      
      <el-menu-item index="/">
        <i class="el-icon-s-home"></i>
        <span slot="title">控制台</span>
      </el-menu-item>
      
      <el-menu-item index="/users" v-if="hasPermission('user:view')">
        <i class="el-icon-user"></i>
        <span slot="title">用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="/roles" v-if="hasPermission('role:view')">
        <i class="el-icon-s-custom"></i>
        <span slot="title">角色管理</span>
      </el-menu-item>
      
      <el-menu-item index="/permissions" v-if="hasPermission('permission:view')">
        <i class="el-icon-lock"></i>
        <span slot="title">权限管理</span>
      </el-menu-item>
      
      <el-menu-item index="/tenants" v-if="hasPermission('tenant:view')">
        <i class="el-icon-office-building"></i>
        <span slot="title">租户管理</span>
      </el-menu-item>
      
      <div class="sidebar-footer" @click="toggleCollapse">
        <i :class="isCollapse ? 'el-icon-d-arrow-right' : 'el-icon-d-arrow-left'"></i>
      </div>
    </el-menu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'Sidebar',
  data() {
    return {
      isCollapse: false
    };
  },
  computed: {
    ...mapGetters(['hasPermission']),
    
    activeMenu() {
      const { path } = this.$route;
      return path;
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    }
  }
};
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background-color: #304156;
  transition: width 0.28s;
}

.sidebar-menu {
  height: 100%;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 210px;
}

.logo-container {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #434c5e;
}

.logo-title {
  letter-spacing: 1px;
}

.logo-icon {
  font-size: 16px;
}

.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: #bfcbd9;
  cursor: pointer;
  background-color: #263445;
  transition: all 0.3s;
}

.sidebar-footer:hover {
  background-color: #1f2d3d;
  color: #fff;
}
</style>
