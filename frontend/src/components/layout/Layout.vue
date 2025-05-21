<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <Sidebar />
    </div>
    <div class="main-container">
      <div class="header">
        <Header />
      </div>
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { Sidebar, Header } from './index'

export default {
  name: 'Layout',
  components: {
    Sidebar,
    Header
  }
}
</script>

<style scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.28s;
  overflow-y: auto;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 50px;
  line-height: 50px;
  border-bottom: 1px solid #e6e6e6;
  background: #fff;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>