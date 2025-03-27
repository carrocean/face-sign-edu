<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="100" :src="userInfo.avatar">
              {{ userInfo.name?.charAt(0) }}
            </el-avatar>
            <h2>{{ userInfo.name }}</h2>
            <p class="role-tag">学生</p>
          </div>
          <el-divider />
          <div class="profile-info">
            <p><i class="el-icon-user"></i> 学号：{{ userInfo.studentId }}</p>
            <p><i class="el-icon-college"></i> 学院：{{ userInfo.college }}</p>
            <p><i class="el-icon-collection"></i> 专业：{{ userInfo.major }}</p>
            <p><i class="el-icon-date"></i> 年级：{{ userInfo.grade }}</p>
            <p><i class="el-icon-phone"></i> 手机：{{ userInfo.phone }}</p>
            <p><i class="el-icon-message"></i> 邮箱：{{ userInfo.email }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEdit">编辑</el-button>
            </div>
          </template>

          <!-- 编辑表单 -->
          <el-form
            v-if="isEditing"
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
          >
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">保存</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>

          <!-- 展示信息 -->
          <div v-else class="info-display">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
              <el-descriptions-item label="学号">{{ userInfo.studentId }}</el-descriptions-item>
              <el-descriptions-item label="学院">{{ userInfo.college }}</el-descriptions-item>
              <el-descriptions-item label="专业">{{ userInfo.major }}</el-descriptions-item>
              <el-descriptions-item label="年级">{{ userInfo.grade }}</el-descriptions-item>
              <el-descriptions-item label="手机">{{ userInfo.phone }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <el-card class="mt-20">
          <template #header>
            <div class="card-header">
              <span>人脸识别数据</span>
              <el-button type="primary" @click="handleUpdateFace">更新人脸数据</el-button>
            </div>
          </template>

          <div class="face-data-container">
            <div class="face-preview">
              <img v-if="faceData" :src="faceData" alt="人脸数据预览" />
              <div v-else class="no-face-data">
                <i class="el-icon-camera"></i>
                <p>暂无人脸数据</p>
              </div>
            </div>
            <div class="face-info">
              <p>最后更新时间：{{ faceUpdateTime || '暂无' }}</p>
              <p>状态：<el-tag :type="faceData ? 'success' : 'warning'">
                {{ faceData ? '已录入' : '未录入' }}
              </el-tag></p>
            </div>
          </div>
        </el-card>

        <el-card class="mt-20">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
            </div>
          </template>

          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 更新人脸数据对话框 -->
    <el-dialog
      v-model="faceDialogVisible"
      title="更新人脸数据"
      width="500px"
    >
      <div class="face-capture-container">
        <div class="camera-container">
          <video ref="videoRef" autoplay playsinline></video>
        </div>
        <div class="capture-tips">
          <p>请确保：</p>
          <ul>
            <li>面部正对摄像头</li>
            <li>光线充足</li>
            <li>保持自然表情</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="faceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="captureFace" :loading="capturing">
            拍照
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFaceData, registerFace, updateFace, verifyFace } from '@/api/face'
import { useUserStore } from '@/stores/user'

// 用户信息
const userStore = useUserStore()
const userInfo = reactive({
  name: '',
  studentId: '',
  college: '',
  major: '',
  grade: '',
  phone: '',
  email: '',
  avatar: ''
})

// 表单相关
const isEditing = ref(false)
const formRef = ref(null)
const form = reactive({
  name: '',
  phone: '',
  email: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 人脸识别相关
const faceDialogVisible = ref(false)
const capturing = ref(false)
const videoRef = ref(null)
const faceData = ref(null)
const faceUpdateTime = ref(null)
const canvasRef = ref(null)
let stream = null

// 获取人脸数据
const fetchFaceData = async () => {
  try {
    const res = await getFaceData(userStore.userId)
    if (res.code === 200) {
      faceData.value = res.data.imageUrl
      faceUpdateTime.value = res.data.updateTime
    }
  } catch (error) {
    console.error('获取人脸数据失败:', error)
  }
}

// 编辑个人信息
const handleEdit = () => {
  Object.assign(form, {
    name: userInfo.name,
    phone: userInfo.phone,
    email: userInfo.email
  })
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用后端API更新个人信息
        Object.assign(userInfo, form)
        ElMessage.success('更新成功')
        isEditing.value = false
      } catch (error) {
        ElMessage.error('更新失败')
      }
    }
  })
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用后端API修改密码
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('密码修改成功')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 更新人脸数据
const handleUpdateFace = () => {
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

// 拍照
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
    
    // 人脸检测
    const detectRes = await detectFace(imageBase64)
    if (detectRes.code !== 200 || !detectRes.data.face_list?.length) {
      throw new Error('未检测到人脸')
    }
    
    // 人脸质量检测
    const faceQuality = detectRes.data.face_list[0].quality
    if (faceQuality.blur > 0.7 || faceQuality.occlusion.left_eye > 0.6 || 
        faceQuality.occlusion.right_eye > 0.6 || faceQuality.occlusion.nose > 0.7 || 
        faceQuality.occlusion.mouth > 0.7 || faceQuality.occlusion.left_cheek > 0.8 || 
        faceQuality.occlusion.right_cheek > 0.8 || faceQuality.occlusion.chin > 0.6) {
      throw new Error('人脸质量不符合要求')
    }
    
    // 注册或更新人脸
    const groupId = 'student_group'
    if (!faceData.value) {
      // 注册人脸
      const registerRes = await registerFace(userStore.userId, imageBase64, groupId)
      if (registerRes.code !== 200) {
        throw new Error('人脸注册失败')
      }
    } else {
      // 更新人脸
      const updateRes = await updateFace(userStore.userId, imageBase64, groupId)
      if (updateRes.code !== 200) {
        throw new Error('人脸更新失败')
      }
    }
    
    // 验证人脸
    const verifyRes = await verifyFace(userStore.userId, imageBase64)
    if (verifyRes.code !== 200 || verifyRes.data.score < 80) {
      throw new Error('人脸验证失败')
    }
    
    // 更新显示
    faceData.value = imageBase64
    faceUpdateTime.value = new Date().toLocaleString()
    ElMessage.success('人脸数据更新成功')
    faceDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '人脸数据更新失败')
  } finally {
    capturing.value = false
  }
}

// 生命周期钩子
onMounted(async () => {
  // 获取用户信息
  const user = userStore.userInfo
  Object.assign(userInfo, user)
  
  // 获取人脸数据
  await fetchFaceData()
})

onUnmounted(() => {
  closeCamera()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
  h2 {
    margin: 10px 0;
  }
  .role-tag {
    color: #666;
    margin: 0;
  }
}

.profile-info {
  text-align: left;
  p {
    margin: 10px 0;
    color: #666;
    i {
      margin-right: 8px;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.face-data-container {
  display: flex;
  gap: 20px;
}

.face-preview {
  width: 200px;
  height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.no-face-data {
  text-align: center;
  color: #909399;
  i {
    font-size: 48px;
    margin-bottom: 10px;
  }
}

.face-info {
  flex: 1;
  p {
    margin: 10px 0;
  }
}

.face-capture-container {
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

  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.capture-tips {
  text-align: left;
  p {
    margin: 0 0 10px;
    font-weight: bold;
  }
  ul {
    margin: 0;
    padding-left: 20px;
    color: #666;
  }
}
</style> 