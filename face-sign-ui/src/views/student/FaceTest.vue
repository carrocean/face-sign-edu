<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-row type="flex" class="row-bg" justify="center" style="text-align:center;margin-top:200px">
          <el-col :span="12">
            <el-button type="primary" @click="startSignIn">开始考勤</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="success" @click="retrySignIn">重新考勤</el-button>
          </el-col>
        </el-row>
      </el-col>

      <el-col :span="12">
        <b>{{ tips }}</b>
        <div>
          <video ref="videoRef" width="500px" height="500px" autoplay="autoplay"></video>
          <canvas ref="canvasRef" width="500px" height="500px" style="display: none;"></canvas>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { signIn } from "@/api/attendanceRecord.js"
import { ElMessage } from "element-plus"

const tips = ref('')
const mediaStreamTrack = ref(null)
const videoRef = ref(null)
const canvasRef = ref(null)

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
  openOrCloseVideo(false)
  openMedia()
}

/**
 * 重新考勤
 */
const retrySignIn = () => {
  startSignIn()
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

// 组件卸载时清理摄像头资源
onUnmounted(() => {
  closeMedia()
})
</script>

<style scoped>
.row-bg {
  padding: 10px 0;
}
</style>