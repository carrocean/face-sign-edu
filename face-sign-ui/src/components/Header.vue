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
import {logout} from "@/api/user.js";
const {proxy} = getCurrentInstance()

const account = ref(proxy.$common.getCookies(proxy.$config.account) || '学生')

function handleCommand (command) {
  if(command === 'logout') {
    logout().then(res => {
      if(res.code === 200) {
        // 清除登录信息
        common.removeCookies(config.tokenKeyName)
        common.removeCookies(config.userRole)
        common.removeCookies(config.account)
        ElMessage.success('退出成功')
        router.push('/login')
      }
    });
  }
}

</script>

<style lang="scss" scoped>
.the-header {
  background-color: #304156;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  
  img {
    width: 40px;
    height: 40px;
    border-radius: 4px;
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.05);
    }
  }
}

.app-name {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 0.5px;
}

.header-right {
  .el-dropdown-link {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #ffffff;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 4px;
    transition: all 0.3s;
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
    }
    
    .el-icon {
      font-size: 16px;
      transition: transform 0.3s;
    }
    
    &:hover .el-icon {
      transform: translateY(1px);
    }
  }
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  .el-dropdown-menu__item {
    padding: 8px 16px;
    font-size: 14px;
    color: #606266;
    transition: all 0.3s;
    
    &:hover {
      background-color: #f5f7fa;
      color: #409EFF;
    }
  }
}
</style>