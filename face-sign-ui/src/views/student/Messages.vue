<template>
  <div class="messages-container">
    <el-card class="message-card">
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
          <el-button type="primary" @click="markAllAsRead">全部标记为已读</el-button>
        </div>
      </template>

      <!-- 消息类型筛选 -->
      <div class="filter-container">
        <el-radio-group v-model="messageType" @change="handleTypeChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="attendance">考勤通知</el-radio-button>
          <el-radio-button label="course">课程通知</el-radio-button>
          <el-radio-button label="system">系统通知</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ 'unread': !message.read }"
          @click="viewMessage(message)"
        >
          <div class="message-icon">
            <el-icon :size="24">
              <component :is="getMessageIcon(message.type)" />
            </el-icon>
          </div>
          <div class="message-content">
            <div class="message-title">
              <span>{{ message.title }}</span>
              <el-tag v-if="!message.read" size="small" type="danger">未读</el-tag>
            </div>
            <div class="message-brief">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="showMessageDetail"
      title="消息详情"
      width="600px"
    >
      <div v-if="currentMessage" class="message-detail">
        <h3>{{ currentMessage.title }}</h3>
        <div class="message-meta">
          <span>发送时间：{{ currentMessage.time }}</span>
          <el-tag size="small" :type="getMessageTypeTag(currentMessage.type)">
            {{ getMessageTypeText(currentMessage.type) }}
          </el-tag>
        </div>
        <div class="message-content">
          {{ currentMessage.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Bell,
  Calendar,
  Reading,
  InfoFilled
} from '@element-plus/icons-vue'

// 消息类型
const messageType = ref('all')

// 模拟数据
const messages = ref([
  {
    id: 1,
    type: 'attendance',
    title: '考勤提醒',
    content: '您今天上午的课程《高等数学》还未打卡，请及时完成考勤。',
    time: '2024-03-28 09:30',
    read: false
  },
  {
    id: 2,
    type: 'course',
    title: '课程变更通知',
    content: '《大学物理》课程时间调整为明天下午2点，请准时参加。',
    time: '2024-03-27 15:20',
    read: true
  },
  {
    id: 3,
    type: 'system',
    title: '系统维护通知',
    content: '系统将于今晚22:00-23:00进行例行维护，期间可能无法使用。',
    time: '2024-03-27 10:00',
    read: false
  }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 消息详情相关
const showMessageDetail = ref(false)
const currentMessage = ref(null)

// 获取消息图标
const getMessageIcon = (type) => {
  const icons = {
    attendance: Calendar,
    course: Reading,
    system: InfoFilled
  }
  return icons[type] || Bell
}

// 获取消息类型标签
const getMessageTypeTag = (type) => {
  const types = {
    attendance: 'warning',
    course: 'success',
    system: 'info'
  }
  return types[type] || 'info'
}

// 获取消息类型文本
const getMessageTypeText = (type) => {
  const texts = {
    attendance: '考勤通知',
    course: '课程通知',
    system: '系统通知'
  }
  return texts[type] || '其他'
}

// 查看消息详情
const viewMessage = (message) => {
  currentMessage.value = message
  showMessageDetail.value = true
  if (!message.read) {
    message.read = true
    // TODO: 调用后端API标记消息为已读
  }
}

// 全部标记为已读
const markAllAsRead = () => {
  messages.value.forEach(message => {
    message.read = true
  })
  // TODO: 调用后端API标记所有消息为已读
  ElMessage.success('已全部标记为已读')
}

// 消息类型筛选
const handleTypeChange = (type) => {
  // TODO: 根据类型筛选消息
  console.log('筛选类型:', type)
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style scoped>
.messages-container {
  padding: 20px;
}

.message-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-container {
  margin-bottom: 20px;
}

.message-list {
  margin-bottom: 20px;
}

.message-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.message-item:hover {
  background-color: #f5f7fa;
}

.message-item.unread {
  background-color: #f0f9ff;
}

.message-icon {
  margin-right: 15px;
  color: #409EFF;
}

.message-content {
  flex: 1;
}

.message-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
  font-weight: bold;
}

.message-brief {
  color: #666;
  margin-bottom: 5px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.message-detail {
  padding: 20px;
}

.message-detail h3 {
  margin-bottom: 15px;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  color: #666;
}

.message-content {
  line-height: 1.6;
  color: #333;
}
</style> 