<template>
  <div class="side-menu-wrapper">
    <!-- collapse 属性：控制菜单收缩展开 -->
    <el-menu
        :default-active="activeMenu"
        class="side-menu"
        :router="true"
        :collapse="isCollapse"
    >
      <el-menu-item index="/teacher/dashboard">
        <el-icon :size="20">
          <House></House>
        </el-icon>
        <span>首页</span>
      </el-menu-item>

      <el-menu-item index="/teacher/profile">
        <el-icon :size="20">
          <User></User>
        </el-icon>
        <span>个人信息</span>
      </el-menu-item>

      <el-menu-item index="/teacher/courses">
        <el-icon :size="20">
          <notebook></notebook>
        </el-icon>
        <span>课程管理</span>
      </el-menu-item>

      <el-menu-item index="/teacher/attendance">
        <el-icon :size="20">
          <Check></Check>
        </el-icon>
        <span>考勤管理</span>
      </el-menu-item>
    </el-menu>

  </div>
</template>

<script setup>
import {
  Check,
  House,
  Notebook,
  User,
} from "@element-plus/icons-vue";
import {computed, onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";

const route = useRoute()
const isCollapse = ref(false) //  控制菜单收缩展开

const activeMenu = route.path;


</script>

<style lang="stylus" scoped>

.side-menu-wrapper {
  position: relative;
  height: calc(100vh - 61px);
  padding-right: 11px;

  .side-menu {
  // 高度设置为屏幕高度减去顶部导航栏的高度 height: calc(100vh - 127px);
    overflow: auto;
  // 调整滚动条样式 setScrollbar(6 px, transparent, #C0C4CC);
    .my-file, .recovery {
      box-shadow: 0 4px 12px 0 $BorderExtralight;
    }
  }

// 对展开状态下的菜单设置宽度

  .side-menu:not(.el-menu--collapse) {
    width: 210px;
    height: calc(100vh - 61px);
  }
}
</style>
