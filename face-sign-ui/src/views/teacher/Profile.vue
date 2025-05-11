<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <div>个人信息</div>
              <div>
                <el-button type="primary" @click="handleEdit">修改密码</el-button>
              </div>
            </div>
          </template>

          <el-row>
            <el-col :span="24">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
                <el-descriptions-item label="工号">{{ userInfo.teacherNumber }}</el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ userInfo.phone }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog
        v-model="passwordDialogVisible"
        title="修改密码"
        width="500px"
    >
      <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password/>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="editLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {getTeacherByUserId, updateTeacher} from '@/api/teacher.js'
import {resetPassword} from "@/api/user.js";

// 用户信息
const userInfo = reactive({
  'name': '',
  'teacherNumber': '',
  'phone': '',
  'email': ''
})

const {proxy} = getCurrentInstance()
const userId = proxy.$common.getCookies(proxy.$config.userId)

// 表单数据
const isEditing = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  name: '',
  teacherId: '',
  department: '',
  title: '',
  phone: '',
  email: ''
})

const editLoading = ref(false)

// 密码验证规则
const passwordRules = {
  oldPassword: [
    {required: true, message: '请输入当前密码', trigger: 'blur'}
  ],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, message: '请确认新密码', trigger: 'blur'},
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


// 密码表单
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 获取用户详情
async function fetchUserDetail() {
  const teacherRes = await getTeacherByUserId(userId)

  if (teacherRes.code === 200) {
    Object.assign(userInfo, teacherRes.data)
  } else {
    ElMessage.error(teacherRes.message || '获取用户详情失败')
  }
}


// 打开编辑对话框
function handleEdit() {
  passwordDialogVisible.value = true
}

// 重置表单
function resetForm() {
  for (let key in passwordForm) {
    delete passwordForm[key]
  }
}

// 取消操作
function handleCancel() {
  resetForm()
  passwordDialogVisible.value = false
}

// 提交编辑
const submitEdit = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {

        let res = await resetPassword(passwordForm)

        if (res.code === 200) {
          ElMessage.success('修改成功')
          passwordDialogVisible.value = false
          // 刷新数据
          resetForm()
          await fetchUserDetail()

        } else {
          ElMessage.error(res.message || '修改失败')
        }
      } catch (error) {
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 初始化
onMounted(() => {
  fetchUserDetail()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  margin-bottom: 20px;
}

.profile-header h2 {
  margin: 10px 0;
  font-size: 20px;
}

.role-tag {
  color: #409EFF;
  font-size: 14px;
}

.profile-info {
  text-align: left;
}

.info-item {
  margin-bottom: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  color: #606266;
  margin-right: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 30px 0;
}
</style>