<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            <el-button type="success" @click="handleStartAttendance">
              <el-icon><VideoCamera /></el-icon>
              开始考勤
            </el-button>
          </div>
        </div>
      </template>

      <!-- 考勤统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>今日考勤率</span>
                <el-icon><DataLine /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.todayAttendanceRate }}%</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>今日缺勤数</span>
                <el-icon><Warning /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.todayAbsenceCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>本周平均出勤率</span>
                <el-icon><TrendCharts /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.weeklyAverageRate }}%</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>异常考勤数</span>
                <el-icon><CircleClose /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.abnormalCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="请选择课程">
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="考勤状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="已签到" value="SIGNED" />
            <el-option label="未签到" value="UNSIGNED" />
            <el-option label="迟到" value="LATE" />
            <el-option label="早退" value="EARLY_LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 考勤记录列表 -->
      <el-table
        v-loading="loading"
        :data="attendanceList"
        style="width: 100%"
        border
      >
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="time" label="时间" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="签到地点" width="180" />
        <el-table-column prop="device" label="签到设备" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
              详情
            </el-button>
            <el-button
              v-if="scope.row.status === 'UNSIGNED'"
              type="success"
              link
              @click="handleManualSign(scope.row)"
            >
              手动签到
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 考勤详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="考勤详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentAttendance.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentAttendance.studentId }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ currentAttendance.className }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ currentAttendance.courseName }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentAttendance.date }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentAttendance.time }}</el-descriptions-item>
        <el-descriptions-item label="考勤状态">
          <el-tag :type="getStatusType(currentAttendance.status)">
            {{ getStatusText(currentAttendance.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签到地点">{{ currentAttendance.location }}</el-descriptions-item>
        <el-descriptions-item label="签到设备">{{ currentAttendance.device }}</el-descriptions-item>
        <el-descriptions-item label="人脸照片" :span="2">
          <el-image
            v-if="currentAttendance.faceImage"
            :src="currentAttendance.faceImage"
            :preview-src-list="[currentAttendance.faceImage]"
            fit="cover"
            style="width: 200px; height: 200px"
          />
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentAttendance.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 手动签到对话框 -->
    <el-dialog
      v-model="showManualSignDialog"
      title="手动签到"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="manualSignFormRef"
        :model="manualSignForm"
        :rules="manualSignRules"
        label-width="100px"
      >
        <el-form-item label="签到状态" prop="status">
          <el-select v-model="manualSignForm.status" placeholder="请选择状态">
            <el-option label="已签到" value="SIGNED" />
            <el-option label="迟到" value="LATE" />
            <el-option label="早退" value="EARLY_LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="签到时间" prop="time">
          <el-time-picker
            v-model="manualSignForm.time"
            placeholder="选择时间"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="manualSignForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showManualSignDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitManualSign" :loading="submitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Download,
  Search,
  Refresh,
  DataLine,
  Warning,
  TrendCharts,
  CircleClose,
  VideoCamera
} from '@element-plus/icons-vue'

// 统计数据
const statistics = reactive({
  todayAttendanceRate: 0,
  todayAbsenceCount: 0,
  weeklyAverageRate: 0,
  abnormalCount: 0
})

// 搜索表单
const searchForm = reactive({
  courseId: '',
  dateRange: [],
  status: ''
})

// 课程选项
const courseOptions = ref([
  { id: 1, name: '高等数学' },
  { id: 2, name: '大学物理' }
])

// 考勤列表数据
const loading = ref(false)
const attendanceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 考勤详情对话框
const showDetailDialog = ref(false)
const currentAttendance = ref({})

// 手动签到对话框
const showManualSignDialog = ref(false)
const submitting = ref(false)
const manualSignFormRef = ref(null)
const manualSignForm = reactive({
  status: '',
  time: '',
  remark: ''
})

// 表单验证规则
const manualSignRules = {
  status: [
    { required: true, message: '请选择签到状态', trigger: 'change' }
  ],
  time: [
    { required: true, message: '请选择签到时间', trigger: 'change' }
  ]
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // TODO: 调用获取统计数据API
    Object.assign(statistics, {
      todayAttendanceRate: 95,
      todayAbsenceCount: 3,
      weeklyAverageRate: 92,
      abnormalCount: 2
    })
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取考勤列表
const fetchAttendanceList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取考勤列表API
    attendanceList.value = [
      {
        id: 1,
        studentName: '张三',
        studentId: '2024001',
        className: '计算机1班',
        courseName: '高等数学',
        date: '2024-03-28',
        time: '08:30:00',
        status: 'SIGNED',
        location: '教学楼A101',
        device: '摄像头1',
        faceImage: 'https://example.com/face.jpg',
        remark: ''
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取考勤列表失败')
  } finally {
    loading.value = false
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'SIGNED': 'success',
    'UNSIGNED': 'danger',
    'LATE': 'warning',
    'EARLY_LEAVE': 'warning'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'SIGNED': '已签到',
    'UNSIGNED': '未签到',
    'LATE': '迟到',
    'EARLY_LEAVE': '早退'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAttendanceList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    courseId: '',
    dateRange: [],
    status: ''
  })
  handleSearch()
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出考勤数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 开始考勤
const handleStartAttendance = () => {
  // TODO: 跳转到人脸识别考勤页面
}

// 查看详情
const handleViewDetail = (row) => {
  currentAttendance.value = row
  showDetailDialog.value = true
}

// 手动签到
const handleManualSign = (row) => {
  currentAttendance.value = row
  showManualSignDialog.value = true
}

// 提交手动签到
const handleSubmitManualSign = async () => {
  if (!manualSignFormRef.value) return
  await manualSignFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用手动签到API
        ElMessage.success('签到成功')
        showManualSignDialog.value = false
        fetchAttendanceList()
      } catch (error) {
        ElMessage.error('签到失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAttendanceList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAttendanceList()
}

onMounted(() => {
  fetchStatistics()
  fetchAttendanceList()
})
</script>

<style scoped>
.attendance-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.statistics-card {
  .statistics-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .statistics-value {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
    text-align: center;
    margin-top: 10px;
  }
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>