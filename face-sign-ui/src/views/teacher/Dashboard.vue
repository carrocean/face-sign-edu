<template>
  <div class="dashboard-container">
    <!-- 数据统计卡片 -->
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
            <div class="number">{{ statistics.todayCourses }}</div>
            <div class="label">节</div>
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
            <div class="number">{{ statistics.pendingSignIn }}</div>
            <div class="label">节</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>今日出勤率</span>
              <el-icon><DataLine /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.attendanceRate }}%</div>
            <div class="label">平均</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总学生数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.totalStudents }}</div>
            <div class="label">人</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <!-- 今日课程 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日课程</span>
            </div>
          </template>
          <el-table :data="todayCourses" style="width: 100%">
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="time" label="上课时间" width="180" />
            <el-table-column prop="location" label="上课地点" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button
                  v-if="scope.row.status === 'pending'"
                  type="primary"
                  @click="handleStartSignIn(scope.row)"
                >开始签到</el-button>
                <el-button
                  v-else-if="scope.row.status === 'active'"
                  type="success"
                  @click="handleViewAttendance(scope.row)"
                >查看考勤</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <!-- 考勤统计 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>本周考勤统计</span>
            </div>
          </template>
          <div class="attendance-chart">
            <div
              v-for="(item, index) in weeklyAttendance"
              :key="index"
              class="chart-item"
            >
              <div class="chart-bar">
                <div
                  class="bar-fill"
                  :style="{ height: item.rate + '%' }"
                ></div>
              </div>
              <div class="chart-label">{{ item.day }}</div>
            </div>
          </div>
          <div class="attendance-legend">
            <div class="legend-item">
              <span class="dot high"></span>
              <span>90%以上</span>
            </div>
            <div class="legend-item">
              <span class="dot medium"></span>
              <span>80-90%</span>
            </div>
            <div class="legend-item">
              <span class="dot low"></span>
              <span>80%以下</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar,
  Timer,
  DataLine,
  User
} from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const statistics = ref({
  todayCourses: 0,
  pendingSignIn: 0,
  attendanceRate: 0,
  totalStudents: 0
})

// 今日课程
const todayCourses = ref([])

// 周考勤数据
const weeklyAttendance = ref([])

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'active': 'success',
    'ended': 'info'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'pending': '待开始',
    'active': '进行中',
    'ended': '已结束'
  }
  return texts[status] || status
}

// 开始签到
const handleStartSignIn = (course) => {
  router.push({
    path: '/teacher/attendance',
    query: { courseId: course.id }
  })
}

// 查看考勤
const handleViewAttendance = (course) => {
  router.push({
    path: '/teacher/attendance',
    query: { courseId: course.id, view: 'true' }
  })
}

// 获取仪表盘数据
const fetchDashboardData = () => {
  // TODO: 调用后端API获取数据
  // 模拟数据
  statistics.value = {
    todayCourses: 4,
    pendingSignIn: 2,
    attendanceRate: 95,
    totalStudents: 120
  }

  todayCourses.value = [
    {
      id: 1,
      name: '高等数学',
      time: '08:00-09:40',
      location: '教学楼A101',
      status: 'active'
    },
    {
      id: 2,
      name: '大学物理',
      time: '10:00-11:40',
      location: '教学楼B202',
      status: 'pending'
    }
  ]

  weeklyAttendance.value = [
    { day: '周一', rate: 95 },
    { day: '周二', rate: 92 },
    { day: '周三', rate: 88 },
    { day: '周四', rate: 96 },
    { day: '周五', rate: 90 }
  ]
}

onMounted(() => {
  fetchDashboardData()
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