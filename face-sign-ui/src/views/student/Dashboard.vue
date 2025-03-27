<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>今日课程</span>
              <el-icon><Calendar /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.todayCourses }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>本周考勤率</span>
              <el-icon><DataLine /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.attendanceRate }}%</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>待签到课程</span>
              <el-icon><Timer /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.pendingSignIn }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总课程数</span>
              <el-icon><Reading /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.totalCourses }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日课程安排</span>
            </div>
          </template>
          <el-table :data="todayCourses" style="width: 100%">
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="teacher" label="授课教师" />
            <el-table-column prop="time" label="上课时间" />
            <el-table-column prop="location" label="上课地点" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近考勤记录</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(record, index) in recentAttendance"
              :key="index"
              :timestamp="record.time"
              :type="record.status === 'present' ? 'success' : 'danger'">
              {{ record.courseName }} - {{ record.status === 'present' ? '已签到' : '未签到' }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, DataLine, Timer, Reading } from '@element-plus/icons-vue'

const statistics = ref({
  todayCourses: 0,
  attendanceRate: 0,
  pendingSignIn: 0,
  totalCourses: 0
})

const todayCourses = ref([])
const recentAttendance = ref([])

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'ongoing': 'primary',
    'ended': 'info',
    'absent': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'pending': '待上课',
    'ongoing': '进行中',
    'ended': '已结束',
    'absent': '已缺勤'
  }
  return texts[status] || status
}

onMounted(() => {
  // TODO: 从后端获取统计数据
  // 模拟数据
  statistics.value = {
    todayCourses: 3,
    attendanceRate: 95,
    pendingSignIn: 1,
    totalCourses: 8
  }

  todayCourses.value = [
    {
      name: '高等数学',
      teacher: '张老师',
      time: '08:00-09:40',
      location: '教学楼A101',
      status: 'ongoing'
    },
    {
      name: '大学物理',
      teacher: '李老师',
      time: '10:00-11:40',
      location: '教学楼B202',
      status: 'pending'
    }
  ]

  recentAttendance.value = [
    {
      courseName: '高等数学',
      time: '2024-03-24 08:00',
      status: 'present'
    },
    {
      courseName: '大学英语',
      time: '2024-03-23 14:00',
      status: 'absent'
    }
  ]
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.data-card {
  height: 120px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style> 