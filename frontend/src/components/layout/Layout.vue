<template>
  <div class="app-wrapper">
    <Sidebar class="sidebar-container" />
    <div class="main-container">
      <Header />
      <div class="app-main">
        <transition name="fade-transform" mode="out-in">
          <router-view :key="key" />
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import { Sidebar, Header } from '@/components/layout'

export default {
  name: 'Layout',
  components: {
    Sidebar,
    Header
  },
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}

.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 210px;
  overflow: hidden;
  background: #304156;
  transition: width 0.28s;
  z-index: 1001;
}

.main-container {
  min-height: 100%;
  margin-left: 210px;
  position: relative;
  transition: margin-left 0.28s;
}

.app-main {
  padding: 20px;
}

/* fade-transform */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
