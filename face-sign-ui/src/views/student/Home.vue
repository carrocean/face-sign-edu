<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24" :xs="{span: 0}">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日课程安排</span>
            </div>
          </template>
          <el-table :data="todayCourses" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" :responsive="[{ hide: false, when: 'xs' }]"/>
            <el-table-column prop="teacherName" label="授课教师" :responsive="[{ hide: true, when: 'xs' }]"/>
            <el-table-column label="上课时间" :responsive="[{ hide: true, when: 'xs' }]">
              <template #default="scope">
                {{ getPeriodTime(scope.row.period) }}
              </template>
            </el-table-column>
            <el-table-column prop="classroom" label="上课地点" :responsive="[{ hide: true, when: 'xs' }]"/>
            <el-table-column prop="status" label="状态" :responsive="[{ hide: false, when: 'xs' }]">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

    </el-row>
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24" style="text-align: center; padding-top: 50px">
        <el-button type="primary" style=" bottom: 100px; width: 200px; height: 60px; font-size: 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);" @click="toFaceSign">签到</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, DataLine, Timer, Reading } from '@element-plus/icons-vue'
import {getTodayCourseByStudent} from '@/api/courseSchedule.js'
import {ElMessage} from "element-plus";
import { useRouter } from 'vue-router'

const statistics = ref({
  todayCourses: 0,
  attendanceRate: 0,
  pendingSignIn: 0,
  totalCourses: 0
})

const todayCourses = ref([])
const recentAttendance = ref([])
const router = useRouter()

// 根据节次获取上课时间
const getPeriodTime = (period) => {
  const periodTimes = {
    1: '08:00-09:40',
    2: '10:00-11:40',
    3: '14:30-16:05',
    4: '16:25-18:00'
  };
  return periodTimes[period] || '未知节次';
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'ongoing': 'primary',
    'ended': 'info'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'pending': '待上课',
    'ongoing': '进行中',
    'ended': '已结束'
  }
  return texts[status] || status
}

// 获取课程安排
async function fetchCourseSchedules() {
  try {
    const res = await getTodayCourseByStudent()
    todayCourses.value = res.data
  } catch (error) {
    console.error('获取课程安排失败:', error)
    ElMessage.error('获取课程安排失败')
  }
}

const toFaceSign = () => {
  router.push('/student/sign')
}

onMounted(() => {
  fetchCourseSchedules()
  // TODO: 从后端获取统计数据
  // 模拟数据
  statistics.value = {
    todayCourses: 3,
    attendanceRate: 95,
    pendingSignIn: 1,
    totalCourses: 8
  }



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