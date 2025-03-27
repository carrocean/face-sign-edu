<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { searchFace } from '@/api/face'
import { startAttendance, endAttendance, getAttendanceList, getAttendanceDetail } from '@/api/attendance'

// 搜索表单
const searchForm = reactive({
  courseId: '',
  date: ''
})

// 表格数据
const loading = ref(false)
const attendanceList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 课程选项
const courseOptions = ref([
  { id: 1, name: '高等数学' },
  { id: 2, name: '大学物理' }
])

// 开始考勤相关
const startDialogVisible = ref(false)
const submitting = ref(false)
const startFormRef = ref(null)
const startForm = reactive({
  courseId: '',
  time: '',
  location: '',
  duration: 30
})

// 开始考勤表单验证规则
const startRules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  time: [
    { required: true, message: '请选择考勤时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入考勤地点', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入考勤时长', trigger: 'blur' }
  ]
}

// 考勤详情相关
const detailDialogVisible = ref(false)
const currentAttendance = ref(null)
const attendanceDetail = ref([])

// 人脸识别考勤
const faceDialogVisible = ref(false)
const capturing = ref(false)
const videoRef = ref(null)
const canvasRef = ref(null)
let stream = null

// 获取进度条状态
const getProgressStatus = (rate) => {
  if (rate >= 90) return 'success'
  if (rate >= 80) return 'warning'
  return 'exception'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'present': 'success',
    'absent': 'danger',
    'late': 'warning'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'present': '已签到',
    'absent': '未签到',
    'late': '迟到'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  fetchAttendanceList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.courseId = ''
  searchForm.date = ''
  handleSearch()
}

// 获取考勤记录列表
const fetchAttendanceList = async () => {
  loading.value = true
  try {
    const res = await getAttendanceList({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200) {
      attendanceList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取考勤列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 开始考勤
const handleStartAttendance = async () => {
  try {
    const res = await startAttendance()
    if (res.code === 200) {
      ElMessage.success('考勤开始')
      fetchAttendanceList()
    }
  } catch (error) {
    ElMessage.error('开始考勤失败')
  }
}

// 结束考勤
const handleEndAttendance = async (attendanceId) => {
  try {
    await ElMessageBox.confirm('确定要结束本次考勤吗?', '提示', {
      type: 'warning'
    })
    const res = await endAttendance(attendanceId)
    if (res.code === 200) {
      ElMessage.success('考勤结束')
      fetchAttendanceList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('结束考勤失败')
    }
  }
}

// 查看考勤详情
const handleViewDetail = async (attendanceId) => {
  try {
    const res = await getAttendanceDetail(attendanceId)
    if (res.code === 200) {
      currentAttendance.value = attendanceList.value.find(item => item.id === attendanceId)
      attendanceDetail.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取考勤详情失败')
  }
}

// 打开人脸识别考勤
const handleFaceAttendance = () => {
  faceDialogVisible.value = true
  openCamera()
}

// 打开摄像头
const openCamera = async () => {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ 
      video: {
        width: 640,
        height: 480,
        facingMode: 'user'
      } 
    })
    videoRef.value.srcObject = stream
  } catch (error) {
    ElMessage.error('无法访问摄像头')
    faceDialogVisible.value = false
  }
}

// 关闭摄像头
const closeCamera = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
}

// 拍照识别
const captureFace = async () => {
  capturing.value = true
  try {
    // 创建canvas并绘制视频帧
    const canvas = document.createElement('canvas')
    canvas.width = videoRef.value.videoWidth
    canvas.height = videoRef.value.videoHeight
    canvas.getContext('2d').drawImage(videoRef.value, 0, 0)
    
    // 转换为base64
    const imageBase64 = canvas.toDataURL('image/jpeg').split(',')[1]
    
    // 人脸搜索
    const groupId = 'student_group'
    const searchRes = await searchFace(imageBase64, groupId)
    if (searchRes.code !== 200 || !searchRes.data.user_list?.length) {
      throw new Error('未识别到人脸')
    }
    
    // 获取最佳匹配结果
    const bestMatch = searchRes.data.user_list[0]
    if (bestMatch.score < 80) {
      throw new Error('人脸匹配度不足')
    }
    
    // TODO: 调用考勤记录API
    ElMessage.success('考勤成功')
    faceDialogVisible.value = false
    fetchAttendanceList()
  } catch (error) {
    ElMessage.error(error.message || '考勤失败')
  } finally {
    capturing.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  fetchAttendanceList()
})

onUnmounted(() => {
  closeCamera()
})

// 导出考勤记录
const handleExport = (row) => {
  // TODO: 调用后端API导出考勤记录
  ElMessage.success('导出成功')
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAttendanceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAttendanceList()
}
</script>

<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤管理</span>
          <el-button type="primary" @click="handleStartAttendance">开始考勤</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable>
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 考勤记录表格 -->
      <el-table :data="attendanceList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="time" label="时间" width="120" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="totalStudents" label="应到人数" width="100" />
        <el-table-column prop="presentCount" label="实到人数" width="100" />
        <el-table-column prop="attendanceRate" label="出勤率" width="120">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.attendanceRate"
              :status="getProgressStatus(scope.row.attendanceRate)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row.id)">查看详情</el-button>
            <el-button type="primary" link @click="handleExport(scope.row)">导出</el-button>
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

    <!-- 开始考勤对话框 -->
    <el-dialog
      v-model="startDialogVisible"
      title="开始考勤"
      width="600px"
    >
      <el-form
        ref="startFormRef"
        :model="startForm"
        :rules="startRules"
        label-width="100px"
      >
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="startForm.courseId" placeholder="请选择课程">
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤时间" prop="time">
          <el-time-picker
            v-model="startForm.time"
            format="HH:mm"
            placeholder="选择时间"
          />
        </el-form-item>
        <el-form-item label="考勤地点" prop="location">
          <el-input v-model="startForm.location" />
        </el-form-item>
        <el-form-item label="考勤时长" prop="duration">
          <el-input-number v-model="startForm.duration" :min="1" :max="120" />
          <span class="duration-unit">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="startDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStartSubmit" :loading="submitting">
            开始考勤
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 考勤详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="考勤详情"
      width="800px"
    >
      <div class="detail-header">
        <div class="detail-item">
          <span class="label">课程名称：</span>
          <span>{{ currentAttendance.courseName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">考勤时间：</span>
          <span>{{ currentAttendance.date }} {{ currentAttendance.time }}</span>
        </div>
        <div class="detail-item">
          <span class="label">考勤地点：</span>
          <span>{{ currentAttendance.location }}</span>
        </div>
        <div class="detail-item">
          <span class="label">出勤率：</span>
          <el-progress
            :percentage="currentAttendance.attendanceRate"
            :status="getProgressStatus(currentAttendance.attendanceRate)"
            style="width: 200px; display: inline-block"
          />
        </div>
      </div>
      <el-table :data="attendanceDetail" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="签到时间" width="180" />
        <el-table-column prop="location" label="签到地点" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 人脸识别考勤对话框 -->
    <el-dialog
      v-model="faceDialogVisible"
      title="人脸识别考勤"
      width="800px"
    >
      <div class="video-container">
        <video ref="videoRef" width="640" height="480" autoplay></video>
        <canvas ref="canvasRef" width="640" height="480"></canvas>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="faceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="captureFace" :loading="capturing">拍照识别</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.attendance-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.duration-unit {
  margin-left: 10px;
}

.detail-header {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.detail-item {
  margin-bottom: 10px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item .label {
  font-weight: bold;
  margin-right: 10px;
}

.video-container {
  position: relative;
  width: 100%;
  height: 480px;
}

video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>