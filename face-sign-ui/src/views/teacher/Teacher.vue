<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/teacher/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/teacher/courses">
          <el-icon><Reading /></el-icon>
          <span>我的课程</span>
        </el-menu-item>
        <el-menu-item index="/teacher/attendance">
          <el-icon><Calendar /></el-icon>
          <span>考勤管理</span>
        </el-menu-item>
        <el-menu-item index="/teacher/notifications">
          <el-icon><Message /></el-icon>
          <span>通知管理</span>
        </el-menu-item>
        <el-menu-item index="/teacher/leave-approval">
          <el-icon><Document /></el-icon>
          <span>请假审批</span>
        </el-menu-item>
        <el-menu-item index="/teacher/profile">
          <el-icon><User /></el-icon>
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
import { ElMessageBox } from 'element-plus'
import {
  DataLine,
  Reading,
  Calendar,
  User,
  Fold,
  Expand,
  SwitchButton,
  Message,
  Document
} from '@element-plus/icons-vue'
// Remove Breadcrumb import

const router = useRouter()
const route = useRoute()

// 侧边栏折叠状态
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 用户信息
const userInfo = ref({
  name: '张老师',
  avatar: ''
})

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用退出登录API
    router.push('/login')
  })
}
</script>

<style lang="scss">
@import '@/styles/layout.scss';
</style>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;

  img {
    width: 32px;
    height: 32px;
    margin-right: 12px;
  }
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;

  .toggle-sidebar {
    font-size: 20px;
    cursor: pointer;
    color: #666;
    &:hover {
      color: #409EFF;
    }
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 8px;

    .account {
      margin-left: 8px;
      color: #666;
    }
  }
}
</style>
