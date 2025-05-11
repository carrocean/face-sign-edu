<template>
  <div class="container">
    <el-select v-model="selectedCourse" placeholder="请选择课程" style="width: 200px;">
      <el-option
        v-for="item in filteredCourses"
        :key="item.id"
        :label="`${item.courseName} (${getPeriodTime(item.period).start}-${getPeriodTime(item.period).end} ${item.teacherName})`"
        :value="item"
      />
    </el-select>
    <div class="video-container">
      <video ref="videoRef" width="500px" height="500px" autoplay="autoplay"></video>
    </div>
    <el-button type="primary" @click="startSignIn" style="width:200px;height:50px;padding:15px 20px;font-size:18px;">开始考勤</el-button>
    <canvas ref="canvasRef" width="500px" height="500px" style="display: none;"></canvas>
  </div>
</template>

<script setup>
import { ref, onUnmounted, onMounted, computed } from 'vue'
import { signIn } from "@/api/attendanceRecord.js"
import { ElMessage } from "element-plus"
import { getTodayCourseByStudent } from "@/api/courseSchedule.js";
import dayjs from 'dayjs'

const tips = ref('')
const mediaStreamTrack = ref(null)
const videoRef = ref(null)
const canvasRef = ref(null)
const todayCourses = ref([])
const selectedCourse = ref(null)

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

// 根据节次获取上课时间
const getPeriodTime = (period) => {
  const periodTimes = {
    1: { start: '08:00', end: '09:40' },
    2: { start: '10:00', end: '11:40' },
    3: { start: '14:30', end: '16:05' },
    4: { start: '16:25', end: '18:00' }
  };
  return periodTimes[period] || { start: '00:00', end: '23:59' };
}

// 过滤可用课程
const filteredCourses = computed(() => {
  return todayCourses.value.filter(course => {
    return course.status === 'ongoing'; // 只返回状态为 ongoing 的课程
  });
});

/**
 * 打开摄像头
 */
const openMedia = async () => {
  tips.value = '正在打开摄像头'
  const constraints = { video: { width: 500, height: 500 }, audio: false }
  
  try {
    const mediaStream = await navigator.mediaDevices.getUserMedia(constraints)
    mediaStreamTrack.value = typeof mediaStream.stop === 'function' ? mediaStream : mediaStream.getTracks()[0]
    videoRef.value.srcObject = mediaStream
    await videoRef.value.play()
    tips.value = '请正视摄像头'
    setTimeout(() => photograph(), 2000)
  } catch (error) {
    console.log(error)
    tips.value = '摄像头启动失败，请检查权限设置'
  }
}

/**
 * 关闭摄像头
 */
const closeMedia = () => {
  if (mediaStreamTrack.value) {
    mediaStreamTrack.value.stop()
    openOrCloseVideo(true)
    tips.value = '操作完成'
  }
}

/**
 * 视频框是否隐藏
 */
const openOrCloseVideo = (val) => {
  if (val) {
    videoRef.value.style.display = 'none'
  } else {
    videoRef.value.style.display = 'block'
  }
}

/**
 * 开始考勤
 */
const startSignIn = () => {
  if (!selectedCourse.value) {
    ElMessage.warning('请选择课程')
    return
  }
  openOrCloseVideo(false)
  openMedia()
}

/**
 * base64转文件
 */
const base64ToFile = (base64, fileName) => {
  const data = base64.split(',')
  const type = data[0].match(/:(.*?);/)[1]
  const suffix = type.split('/')[1]
  const bstr = window.atob(data[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  const cleanName = fileName.split('.')[0]
  return new File([u8arr], `${cleanName}.${suffix}`, { type })
}

/**
 * 拍照进行考勤
 */
const photograph = () => {
  const video = videoRef.value
  const canvas = canvasRef.value
  const ctx = canvas.getContext('2d')
  ctx.drawImage(video, 0, 0, 500, 500)
  
  const img = canvas.toDataURL('image/jpeg')
  const imageFile = base64ToFile(img, 'face')

  const formData = new FormData()
  formData.append('image', imageFile)
  formData.append('scheduleId', selectedCourse.value.scheduleId)
  formData.append('courseId', selectedCourse.value.courseId)

  tips.value = '正在识别'

  signIn(formData)
    .then(res => {
      if (res.code === 200) {
        ElMessage.success(res.data)
      } else {
        ElMessage.error(res.msg)
      }
    })
    .catch(error => {
      console.error('考勤失败:', error)
      ElMessage.error("考勤失败，请重试")
    })
    .finally(() => {
      closeMedia()
    })
}

// 获取今日课程安排
async function fetchCourseSchedules() {
  try {
    const res = await getTodayCourseByStudent()
    todayCourses.value = res.data
  } catch (error) {
    console.error('获取课程安排失败:', error)
    ElMessage.error('获取课程安排失败')
  }
}

// 组件卸载时清理摄像头资源
onUnmounted(() => {
  closeMedia()
})

onMounted(() => {
  fetchCourseSchedules()
})
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.video-container {
  width: 500px;
  height: 500px;
  border: 1px solid #ccc;
  background-color: #f5f5f5;
  border-radius: 10px;
  margin: 20px 0;
}

@media (max-width: 768px) {
  .video-container {
    width: 300px;
    height: 300px;
  }

  .video-container video {
    width: 300px;
    height: 300px;
  }
  .el-button {
    width: 150px;
    height: 40px;
    font-size: 16px;
  }
}
</style>