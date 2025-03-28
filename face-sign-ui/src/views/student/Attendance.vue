<template>
  <div class="attendance-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤记录</span>
              <el-button type="primary" @click="showExportDialog = true">
                <el-icon><Download /></el-icon>
                导出考勤记录
              </el-button>
            </div>
          </template>

          <!-- 搜索栏 -->
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="课程">
              <el-select v-model="searchForm.courseId" placeholder="请选择课程" filterable>
                <el-option
                  v-for="course in courseList"
                  :key="course.id"
                  :label="course.name"
                  :value="course.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker
                v-model="searchForm.date"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 考勤记录表格 -->
          <el-table :data="attendanceList" style="width: 100%" v-loading="loading">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="teacherName" label="授课教师" />
            <el-table-column prop="date" label="日期" />
            <el-table-column prop="time" label="时间" />
            <el-table-column prop="location" label="上课地点" />
            <el-table-column prop="status" label="状态">
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
                  @click="handleSignIn(scope.row)"
                >签到</el-button>
                <el-button
                  v-else-if="scope.row.status === 'present'"
                  type="success"
                  disabled
                >已签到</el-button>
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
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日待签到课程</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="course in todayCourses"
              :key="course.id"
              :timestamp="course.time"
              :type="getTimelineType(course.status)">
              <div class="timeline-content">
                <h4>{{ course.name }}</h4>
                <p>地点：{{ course.location }}</p>
                <el-button
                  v-if="course.status === 'pending'"
                  type="primary"
                  size="small"
                  @click="handleSignIn(course)"
                >立即签到</el-button>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 签到对话框 -->
    <el-dialog
      v-model="signInDialogVisible"
      title="人脸签到"
      width="500px"
    >
      <div class="sign-in-container">
        <div class="camera-container">
          <video ref="videoRef" autoplay playsinline></video>
          <canvas ref="canvasRef" class="hidden-canvas"></canvas>
          <div class="face-detection-status" :class="{ 'detected': faceDetected }">
            {{ faceDetected ? '人脸已检测' : '请将人脸对准摄像头' }}
          </div>
        </div>
        <div class="sign-in-info">
          <p>课程：{{ currentCourse.name }}</p>
          <p>时间：{{ currentCourse.time }}</p>
          <p>地点：{{ currentCourse.location }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="signInDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmSignIn" 
            :loading="signingIn"
            :disabled="!faceDetected"
          >
            确认签到
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 导出对话框 -->
    <ExportDialog
      v-model:visible="showExportDialog"
      type="attendance"
      :export-function="exportAttendance"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllAttendanceRecords, signIn } from '@/api/attendanceRecord'
import { getStudentCourses } from '@/api/course'
import { Download } from '@element-plus/icons-vue'
import { exportAttendance } from '@/api/export'
import ExportDialog from '@/components/common/ExportDialog.vue'

// 搜索表单
const searchForm = reactive({
  courseId: '',
  date: []
})

// 表格数据
const loading = ref(false)
const attendanceList = ref([
  {
    id: 1,
    courseName: '高等数学',
    teacherName: '张老师',
    date: '2024-03-25',
    time: '08:00-09:40',
    location: '教学楼A101',
    status: 'PRESENT'
  },
  {
    id: 2,
    courseName: '大学英语',
    teacherName: '李老师',
    date: '2024-03-25',
    time: '10:00-11:40',
    location: '教学楼B203',
    status: 'PENDING'
  },
  {
    id: 3,
    courseName: '计算机基础',
    teacherName: '王老师',
    date: '2024-03-24',
    time: '14:00-15:40',
    location: '实验楼C305',
    status: 'ABSENT'
  }
])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 课程列表
const courseList = ref([])

// 今日待签到课程
const todayCourses = ref([
  {
    id: 2,
    name: '大学英语',
    time: '10:00-11:40',
    location: '教学楼B203',
    status: 'PENDING'
  }
])

// 签到相关
const signInDialogVisible = ref(false)
const signingIn = ref(false)
const currentCourse = reactive({})
const videoRef = ref(null)
const canvasRef = ref(null)
const faceDetected = ref(false)
let stream = null
let detectInterval = null
const showExportDialog = ref(false)

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'PRESENT': 'success',
    'ABSENT': 'danger',
    'LATE': 'warning'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待签到',
    'PRESENT': '已签到',
    'ABSENT': '未签到',
    'LATE': '迟到'
  }
  return texts[status] || status
}

// 获取时间线类型
const getTimelineType = (status) => {
  const types = {
    'PENDING': 'warning',
    'PRESENT': 'success',
    'ABSENT': 'danger',
    'LATE': 'warning'
  }
  return types[status] || 'info'
}

// 搜索
const handleSearch = () => {
  fetchAttendanceList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.courseId = ''
  searchForm.date = []
  handleSearch()
}

// 获取考勤记录列表
const fetchAttendanceList = async () => {
  // 暂时使用示例数据，后续再对接后端
  loading.value = false
}

// 获取课程列表
const fetchCourseList = async () => {
  try {
    const res = await getStudentCourses()
    if (res.code === 200) {
      courseList.value = res.data
    } else {
      ElMessage.error(res.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

// 获取今日待签到课程
const fetchTodayCourses = async () => {
  // 暂时使用示例数据，后续再对接后端
}

// 打开摄像头
const openCamera = async () => {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: true })
    videoRef.value.srcObject = stream
    // 开始实时人脸检测
    startFaceDetection()
  } catch (error) {
    console.error('打开摄像头失败:', error)
    ElMessage.error('打开摄像头失败')
  }
}

// 关闭摄像头
const closeCamera = () => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  if (detectInterval) {
    clearInterval(detectInterval)
    detectInterval = null
  }
}

// 开始实时人脸检测
const startFaceDetection = () => {
  detectInterval = setInterval(async () => {
    if (!videoRef.value || !canvasRef.value) return
    
    const canvas = canvasRef.value
    const video = videoRef.value
    
    // 设置canvas尺寸
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    
    // 绘制视频帧
    const ctx = canvas.getContext('2d')
    ctx.drawImage(video, 0, 0)
    
    // 获取图像数据
    const imageData = canvas.toDataURL('image/jpeg')
    
    try {
      // 调用人脸检测API
      const detectRes = await detectFace(imageData)
      faceDetected.value = detectRes.data
    } catch (error) {
      console.error('人脸检测失败:', error)
      faceDetected.value = false
    }
  }, 1000) // 每秒检测一次
}

// 打开签到对话框
const handleSignIn = (course) => {
  currentCourse.value = course
  signInDialogVisible.value = true
  openCamera()
}

// 确认签到
const confirmSignIn = async () => {
  if (!faceDetected.value) {
    ElMessage.warning('请确保人脸在摄像头范围内')
    return
  }
  
  try {
    signingIn.value = true
    // 1. 获取视频帧
    const canvas = canvasRef.value
    const imageData = canvas.toDataURL('image/jpeg')

    // 2. 调用签到API
    const signInRes = await signIn({
      scheduleId: currentCourse.value.id,
      image: imageData
    })

    if (signInRes.code === 200) {
      ElMessage.success('签到成功')
      signInDialogVisible.value = false
      fetchAttendanceList()
      fetchTodayCourses()
    } else {
      throw new Error(signInRes.message || '签到失败')
    }
  } catch (error) {
    console.error('签到失败:', error)
    ElMessage.error(error.message || '签到失败')
  } finally {
    signingIn.value = false
  }
}

// 监听对话框关闭
watch(signInDialogVisible, (val) => {
  if (!val) {
    closeCamera()
  }
})

// 组件卸载时关闭摄像头
onUnmounted(() => {
  closeCamera()
})

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAttendanceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAttendanceList()
}

// 初始化
onMounted(() => {
  fetchCourseList()
  fetchAttendanceList()
  fetchTodayCourses()
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

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.timeline-content {
  h4 {
    margin: 0;
  }
  p {
    margin: 5px 0;
    color: #666;
  }
}

.sign-in-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.camera-container {
  width: 400px;
  height: 300px;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
  position: relative;

  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .hidden-canvas {
    display: none;
  }

  .face-detection-status {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 10px;
    background-color: rgba(0, 0, 0, 0.5);
    color: #fff;
    text-align: center;
    transition: all 0.3s;

    &.detected {
      background-color: rgba(67, 160, 71, 0.5);
    }
  }
}

.sign-in-info {
  text-align: center;
  p {
    margin: 5px 0;
    color: #666;
  }
}
</style> 