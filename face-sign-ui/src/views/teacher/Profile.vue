<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 用户信息
const userInfo = ref({
  name: '张老师',
  teacherId: 'T2024001',
  department: '计算机学院',
  title: '副教授',
  phone: '13800138000',
  email: 'zhang@example.com',
  avatar: ''
})

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

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择所属院系', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 院系选项
const departmentOptions = [
  { label: '计算机学院', value: '计算机学院' },
  { label: '信息工程学院', value: '信息工程学院' },
  { label: '经济管理学院', value: '经济管理学院' },
  { label: '外国语学院', value: '外国语学院' }
]

// 职称选项
const titleOptions = [
  { label: '教授', value: '教授' },
  { label: '副教授', value: '副教授' },
  { label: '讲师', value: '讲师' },
  { label: '助教', value: '助教' }
]

// 密码修改相关
const passwordFormRef = ref(null)
const changingPassword = ref(false)
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码修改验证规则
const passwordRules = {
  currentPassword: [
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

// 编辑信息
const handleEdit = () => {
  isEditing.value = true
  Object.assign(form, userInfo.value)
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用后端API保存个人信息
        await new Promise(resolve => setTimeout(resolve, 1000))
        Object.assign(userInfo.value, form)
        isEditing.value = false
        ElMessage.success('保存成功')
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        // TODO: 调用后端API修改密码
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success('密码修改成功')
        passwordFormRef.value.resetFields()
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 初始化
Object.assign(form, userInfo.value)
</script>

<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="100" :src="userInfo.avatar">
              {{ userInfo.name?.charAt(0) }}
            </el-avatar>
            <h2>{{ userInfo.name }}</h2>
            <p class="role-tag">教师</p>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <span class="label">工号：</span>
              <span>{{ userInfo.teacherId }}</span>
            </div>
            <div class="info-item">
              <span class="label">所属院系：</span>
              <span>{{ userInfo.department }}</span>
            </div>
            <div class="info-item">
              <span class="label">职称：</span>
              <span>{{ userInfo.title }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span>{{ userInfo.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span>{{ userInfo.email }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息编辑区域 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEdit">编辑信息</el-button>
            </div>
          </template>

          <!-- 基本信息表单 -->
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            :disabled="!isEditing"
          >
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="工号" prop="teacherId">
              <el-input v-model="form.teacherId" disabled />
            </el-form-item>
            <el-form-item label="所属院系" prop="department">
              <el-select v-model="form.department" placeholder="请选择院系">
                <el-option
                  v-for="item in departmentOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="职称" prop="title">
              <el-select v-model="form.title" placeholder="请选择职称">
                <el-option
                  v-for="item in titleOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="handleSubmit" :loading="submitting">
                保存
              </el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>

          <!-- 密码修改 -->
          <el-divider>密码修改</el-divider>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

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