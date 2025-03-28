<template>
  <el-header class="the-header">
      <div class="header-left logo">
        <!-- 应用图标和名字 -->
        <img src="@/assets/学生考勤.svg" alt="App Logo" style="width: 50px; height: 50px;" />
        <span class="app-name">智能考勤通</span>
      </div>

    <div class="header-right">
      <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link account">
              {{ account }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>

</template>

<script setup>
import {computed, getCurrentInstance, ref} from "vue";
import common from "@/libs/globalFunction/common.js";
import config from "@/config/index.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {ArrowDown} from "@element-plus/icons-vue";
const {proxy} = getCurrentInstance()

const account = ref(proxy.$common.getCookies(proxy.$config.account) || '学生')

function handleCommand (command) {
  if (command === 'profile') {
    router.push('/student/profile')
  } else if (command === 'logout') {
    // 清除登录信息
    common.removeCookies(config.tokenKeyName)
    common.removeCookies(config.userRole)
    common.removeCookies(config.account)
    ElMessage.success('退出成功')
    router.push('/login')
  }
}

</script>

<style scoped>
.the-header {
  background-color: #304156;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid #e6e6e6;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  margin-right: 20px;
}

.app-name {
  font-size: 25px;
  font-weight: bold;
  color: #ffffff;
}

.account {
  color: #ffffff;
}
</style>