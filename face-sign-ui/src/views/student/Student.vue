<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/student/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/student/attendance">
          <el-icon><Calendar /></el-icon>
          <span>考勤打卡</span>
        </el-menu-item>

        <el-menu-item index="/student/courses">
          <el-icon><Reading /></el-icon>
          <span>我的课程</span>
        </el-menu-item>

        <el-menu-item index="/student/leave">
          <el-icon><Document /></el-icon>
          <span>请假管理</span>
        </el-menu-item>

        <el-menu-item index="/student/messages">
          <el-icon><Bell /></el-icon>
          <span>消息通知</span>
        </el-menu-item>

        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>

        <el-menu-item index="/student/help">
          <el-icon><QuestionFilled /></el-icon>
          <span>帮助中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import {
  HomeFilled,
  Calendar,
  Reading,
  Document,
  Bell,
  User,
  QuestionFilled,
  Expand,
  Fold
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => {
  const routeMap = {
    '/student/home': '首页',
    '/student/attendance': '考勤打卡',
    '/student/courses': '我的课程',
    '/student/leave': '请假管理',
    '/student/messages': '消息通知',
    '/student/profile': '个人信息',
    '/student/help': '帮助中心'
  }
  return routeMap[route.path] || '首页'
})

const isCollapse = ref(false)
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 监听窗口大小变化
const handleResize = () => {
  if (window.innerWidth <= 768) {
    isCollapse.value = true
  } else {
    isCollapse.value = false
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  handleResize() // 初始化时执行一次
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss">
@import '@/styles/layout.scss';
</style>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  transition: width 0.3s;
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  overflow: hidden;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

@media screen and (max-width: 768px) {
  .header {
    padding: 0 10px;
  }
  
  .logo {
    padding: 0 10px;
  }
  
  .collapse-btn {
    margin-right: 10px;
  }
}

/* 路由切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 