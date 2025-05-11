<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.totalUsers }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>教师数量</span>
              <el-icon><UserFilled /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.teacherCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>学生数量</span>
              <el-icon><UserFilled /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.studentCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>课程数量</span>
              <el-icon><Reading /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ statistics.courseCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近考勤记录</span>
            </div>
          </template>
          <el-table :data="recentAttendance" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="teacherName" label="教师" />
            <el-table-column prop="studentName" label="学生" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'present' ? 'success' : 'danger'">
                  {{ scope.row.status === 'present' ? '已签到' : '未签到' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(notice, index) in notices"
              :key="index"
              :timestamp="notice.time"
              :type="notice.type">
              {{ notice.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, Reading } from '@element-plus/icons-vue'

const statistics = ref({
  totalUsers: 0,
  teacherCount: 0,
  studentCount: 0,
  courseCount: 0
})

const recentAttendance = ref([])
const notices = ref([
  {
    content: '系统更新完成，新增人脸识别功能',
    time: '2024-03-24 10:00',
    type: 'success'
  },
  {
    content: '新学期课程安排已发布',
    time: '2024-03-23 14:30',
    type: 'primary'
  }
])

onMounted(() => {
  // TODO: 从后端获取统计数据
  // 模拟数据
  statistics.value = {
    totalUsers: 150,
    teacherCount: 30,
    studentCount: 120,
    courseCount: 45
  }

  recentAttendance.value = [
    {
      courseName: '高等数学',
      teacherName: '张老师',
      studentName: '李同学',
      status: 'present',
      time: '2024-03-24 09:30'
    },
    {
      courseName: '大学物理',
      teacherName: '王老师',
      studentName: '刘同学',
      status: 'absent',
      time: '2024-03-24 10:15'
    }
  ]
})
</script>

<style scoped>
.dashboard-container {
  .mt-20 {
    margin-top: 20px;
  }
}

.data-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-content {
    text-align: center;
    padding: 20px 0;

    .number {
      font-size: 36px;
      font-weight: bold;
      color: #303133;
    }

    .label {
      margin-top: 8px;
      color: #909399;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.attendance-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 200px;
  padding: 20px 0;

  .chart-item {
    flex: 1;
    text-align: center;
    margin: 0 10px;

    .chart-bar {
      height: 150px;
      background-color: #f0f2f5;
      border-radius: 4px;
      position: relative;
      margin-bottom: 10px;

      .bar-fill {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background-color: #409EFF;
        border-radius: 4px;
        transition: height 0.3s ease;
      }
    }

    .chart-label {
      color: #606266;
      font-size: 12px;
    }
  }
}

.attendance-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;

  .legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
    font-size: 12px;

    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;

      &.high {
        background-color: #67C23A;
      }

      &.medium {
        background-color: #E6A23C;
      }

      &.low {
        background-color: #F56C6C;
      }
    }
  }
}
</style> 