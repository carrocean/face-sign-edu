<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/students">
          <el-icon><UserFilled /></el-icon>
          <span>学生管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/teachers">
          <el-icon><UserFilled /></el-icon>
          <span>教师管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/classes">
          <el-icon><School /></el-icon>
          <span>班级管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/courses">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/attendance">
          <el-icon><Calendar /></el-icon>
          <span>考勤记录</span>
        </el-menu-item>

        <el-menu-item index="/admin/leave">
          <el-icon><Document /></el-icon>
          <span>请假管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/notifications">
          <el-icon><Bell /></el-icon>
          <span>通知管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/logs">
          <el-icon><Document /></el-icon>
          <span>日志管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/admins">
          <el-icon><UserFilled /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/profile">
          <el-icon><Setting /></el-icon>
          <span>个人信息</span>
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import {
  DataLine,
  User,
  UserFilled,
  Reading,
  Setting,
  School,
  Calendar,
  Document
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path
})

</script>


<style lang="scss">
@import '@/styles/layout.scss';
</style>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
}

.el-menu-vertical {
  height: 100%;
}

.el-header {
  padding: 0;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
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