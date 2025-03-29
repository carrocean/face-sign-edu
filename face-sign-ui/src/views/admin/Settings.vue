<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleSave">
              <el-icon><Check /></el-icon>
              保存设置
            </el-button>
            <el-button type="success" @click="handleBackup">
              <el-icon><Download /></el-icon>
              数据备份
            </el-button>
            <el-button type="warning" @click="handleRestore">
              <el-icon><Upload /></el-icon>
              数据恢复
            </el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 考勤时间设置 -->
        <el-tab-pane label="考勤时间设置" name="attendance">
          <el-form
            ref="attendanceFormRef"
            :model="attendanceForm"
            :rules="attendanceRules"
            label-width="120px"
          >
            <el-form-item label="上班时间" prop="workStartTime">
              <el-time-picker
                v-model="attendanceForm.workStartTime"
                format="HH:mm"
                placeholder="选择上班时间"
              />
            </el-form-item>
            <el-form-item label="下班时间" prop="workEndTime">
              <el-time-picker
                v-model="attendanceForm.workEndTime"
                format="HH:mm"
                placeholder="选择下班时间"
              />
            </el-form-item>
            <el-form-item label="迟到时间" prop="lateTime">
              <el-time-picker
                v-model="attendanceForm.lateTime"
                format="HH:mm"
                placeholder="选择迟到时间"
              />
            </el-form-item>
            <el-form-item label="早退时间" prop="earlyLeaveTime">
              <el-time-picker
                v-model="attendanceForm.earlyLeaveTime"
                format="HH:mm"
                placeholder="选择早退时间"
              />
            </el-form-item>
            <el-form-item label="考勤范围" prop="attendanceRange">
              <el-input-number
                v-model="attendanceForm.attendanceRange"
                :min="0"
                :max="1000"
                :step="10"
                placeholder="请输入考勤范围（米）"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 人脸识别设置 -->
        <el-tab-pane label="人脸识别设置" name="face">
          <el-form
            ref="faceFormRef"
            :model="faceForm"
            :rules="faceRules"
            label-width="120px"
          >
            <el-form-item label="识别阈值" prop="threshold">
              <el-slider
                v-model="faceForm.threshold"
                :min="0"
                :max="1"
                :step="0.01"
                :format-tooltip="formatThreshold"
              />
            </el-form-item>
            <el-form-item label="最小人脸尺寸" prop="minFaceSize">
              <el-input-number
                v-model="faceForm.minFaceSize"
                :min="20"
                :max="200"
                :step="10"
                placeholder="请输入最小人脸尺寸（像素）"
              />
            </el-form-item>
            <el-form-item label="最大人脸尺寸" prop="maxFaceSize">
              <el-input-number
                v-model="faceForm.maxFaceSize"
                :min="20"
                :max="200"
                :step="10"
                placeholder="请输入最大人脸尺寸（像素）"
              />
            </el-form-item>
            <el-form-item label="人脸检测间隔" prop="detectInterval">
              <el-input-number
                v-model="faceForm.detectInterval"
                :min="100"
                :max="5000"
                :step="100"
                placeholder="请输入人脸检测间隔（毫秒）"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 系统参数设置 -->
        <el-tab-pane label="系统参数设置" name="system">
          <el-form
            ref="systemFormRef"
            :model="systemForm"
            :rules="systemRules"
            label-width="120px"
          >
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="systemForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统Logo" prop="systemLogo">
              <el-upload
                class="avatar-uploader"
                action="/api/face/sign/admin/system/upload"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="systemForm.systemLogo" :src="systemForm.systemLogo" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="系统描述" prop="systemDesc">
              <el-input
                v-model="systemForm.systemDesc"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            <el-form-item label="邮件通知" prop="emailNotification">
              <el-switch v-model="systemForm.emailNotification" />
            </el-form-item>
            <el-form-item label="短信通知" prop="smsNotification">
              <el-switch v-model="systemForm.smsNotification" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 数据备份对话框 -->
    <el-dialog
      v-model="showBackupDialog"
      title="数据备份"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="backupForm" label-width="100px">
        <el-form-item label="备份名称">
          <el-input v-model="backupForm.name" placeholder="请输入备份名称" />
        </el-form-item>
        <el-form-item label="备份说明">
          <el-input
            v-model="backupForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入备份说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showBackupDialog = false">取消</el-button>
          <el-button type="primary" @click="handleBackupSubmit" :loading="backingUp">
            开始备份
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 数据恢复对话框 -->
    <el-dialog
      v-model="showRestoreDialog"
      title="数据恢复"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-table :data="backupList" style="width: 100%">
        <el-table-column prop="name" label="备份名称" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" link @click="handleRestoreSubmit(scope.row)">
              恢复
            </el-button>
            <el-button type="danger" link @click="handleDeleteBackup(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Download,
  Upload,
  Plus
} from '@element-plus/icons-vue'

// 当前激活的标签页
const activeTab = ref('attendance')

// 考勤时间表单
const attendanceFormRef = ref(null)
const attendanceForm = reactive({
  workStartTime: null,
  workEndTime: null,
  lateTime: null,
  earlyLeaveTime: null,
  attendanceRange: 100
})

// 考勤时间验证规则
const attendanceRules = {
  workStartTime: [
    { required: true, message: '请选择上班时间', trigger: 'change' }
  ],
  workEndTime: [
    { required: true, message: '请选择下班时间', trigger: 'change' }
  ],
  lateTime: [
    { required: true, message: '请选择迟到时间', trigger: 'change' }
  ],
  earlyLeaveTime: [
    { required: true, message: '请选择早退时间', trigger: 'change' }
  ],
  attendanceRange: [
    { required: true, message: '请输入考勤范围', trigger: 'blur' }
  ]
}

// 人脸识别表单
const faceFormRef = ref(null)
const faceForm = reactive({
  threshold: 0.8,
  minFaceSize: 60,
  maxFaceSize: 120,
  detectInterval: 500
})

// 人脸识别验证规则
const faceRules = {
  threshold: [
    { required: true, message: '请设置识别阈值', trigger: 'change' }
  ],
  minFaceSize: [
    { required: true, message: '请输入最小人脸尺寸', trigger: 'blur' }
  ],
  maxFaceSize: [
    { required: true, message: '请输入最大人脸尺寸', trigger: 'blur' }
  ],
  detectInterval: [
    { required: true, message: '请输入人脸检测间隔', trigger: 'blur' }
  ]
}

// 系统参数表单
const systemFormRef = ref(null)
const systemForm = reactive({
  systemName: '',
  systemLogo: '',
  systemDesc: '',
  emailNotification: true,
  smsNotification: false
})

// 系统参数验证规则
const systemRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' }
  ],
  systemDesc: [
    { required: true, message: '请输入系统描述', trigger: 'blur' }
  ]
}

// 备份相关
const showBackupDialog = ref(false)
const showRestoreDialog = ref(false)
const backingUp = ref(false)
const backupForm = reactive({
  name: '',
  description: ''
})
const backupList = ref([])

// 获取系统设置
const fetchSettings = async () => {
  try {
    // TODO: 调用获取系统设置API
    Object.assign(attendanceForm, {
      workStartTime: new Date(2000, 0, 1, 8, 30),
      workEndTime: new Date(2000, 0, 1, 17, 30),
      lateTime: new Date(2000, 0, 1, 9, 0),
      earlyLeaveTime: new Date(2000, 0, 1, 17, 0),
      attendanceRange: 100
    })
    Object.assign(systemForm, {
      systemName: '智能考勤系统',
      systemLogo: '',
      systemDesc: '基于人脸识别的智能考勤系统',
      emailNotification: true,
      smsNotification: false
    })
  } catch (error) {
    ElMessage.error('获取系统设置失败')
  }
}

// 保存设置
const handleSave = async () => {
  try {
    // TODO: 调用保存系统设置API
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 格式化阈值显示
const formatThreshold = (val) => {
  return (val * 100).toFixed(0) + '%'
}

// 处理Logo上传
const handleLogoSuccess = (response) => {
  if (response.code === 200) {
    systemForm.systemLogo = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传Logo前验证
const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 数据备份
const handleBackup = () => {
  showBackupDialog.value = true
}

// 提交备份
const handleBackupSubmit = async () => {
  if (!backupForm.name) {
    ElMessage.warning('请输入备份名称')
    return
  }
  backingUp.value = true
  try {
    // TODO: 调用数据备份API
    ElMessage.success('备份成功')
    showBackupDialog.value = false
  } catch (error) {
    ElMessage.error('备份失败')
  } finally {
    backingUp.value = false
  }
}

// 数据恢复
const handleRestore = async () => {
  try {
    // TODO: 调用获取备份列表API
    backupList.value = [
      {
        name: '系统初始化备份',
        createTime: '2024-03-28 10:00:00',
        description: '系统初始化时的数据备份'
      }
    ]
    showRestoreDialog.value = true
  } catch (error) {
    ElMessage.error('获取备份列表失败')
  }
}

// 提交恢复
const handleRestoreSubmit = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要恢复该备份吗？恢复后当前数据将被覆盖。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用数据恢复API
    ElMessage.success('恢复成功')
    showRestoreDialog.value = false
    fetchSettings()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
    }
  }
}

// 删除备份
const handleDeleteBackup = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该备份吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用删除备份API
    ElMessage.success('删除成功')
    handleRestore()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchSettings()
})
</script>

<style scoped>
.settings-container {
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

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 