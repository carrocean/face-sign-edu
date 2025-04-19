<template>
  <div class="notification-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <el-button type="primary" @click="handleSendNotification">
            <el-icon><Edit /></el-icon>
            发送通知
          </el-button>
        </div>
      </template>

      <el-table :data="notificationList" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllPageNotifications, deleteNotification } from '@/api/notification'

const notificationList = ref([])

async function fetchNotifications() {
  try {
    const res = await getAllPageNotifications()
    if (res.code === 200) {
      notificationList.value = res.data
    } else {
      ElMessage.error(res.message || '获取通知列表失败')
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知列表失败')
  }
}

const handleSendNotification = () => {
  // TODO: 实现发送通知逻辑
}

const handleEdit = (row) => {
  // TODO: 实现编辑通知逻辑
}

const handleDelete = async (row) => {
  try {
    const res = await deleteNotification(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchNotifications()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notification-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>