<template>
  <div class="user-detail-container">
    <div class="detail-layout">
      <!-- 左侧：角色特有信息卡片 -->
      <el-card class="role-card">
        <template #header>
          <div class="card-header">
            <span>{{ getRoleLabel(userDetail.role) }}信息</span>
            <div class="header-buttons">
              <el-button type="primary" @click="handleEdit">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button @click="goBack">
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
            </div>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <!-- 管理员特有信息（administrator表） -->
          <template v-if="userDetail.role === 'admin'">
            <el-descriptions-item label="管理员ID">{{ roleDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ roleDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ roleDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ roleDetail.email }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(roleDetail.addTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ parseTime(roleDetail.updateTime) }}</el-descriptions-item>
          </template>

          <!-- 教师特有信息（teacher表） -->
          <template v-if="userDetail.role === 'teacher'">
            <el-descriptions-item label="教师ID">{{ roleDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ roleDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="工号">{{ roleDetail.teacherNumber }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ roleDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ roleDetail.email }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(roleDetail.addTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ parseTime(roleDetail.updateTime) }}</el-descriptions-item>
          </template>

          <!-- 学生特有信息（student表） -->
          <template v-if="userDetail.role === 'student'">
            <el-descriptions-item label="学生ID">{{ roleDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ roleDetail.name }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ roleDetail.studentNumber }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ roleDetail.className }}</el-descriptions-item>
            <el-descriptions-item label="人脸图像">
              <el-image 
                v-if="roleDetail.faceImage"
                :src="roleDetail.faceImage"
                style="width: 100px; height: 100px"
                :preview-src-list="[roleDetail.faceImage]"
              />
              <span v-else>未上传</span>
            </el-descriptions-item>
            <el-descriptions-item label="手机号">{{ roleDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ roleDetail.email }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(roleDetail.addTime) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ parseTime(roleDetail.updateTime) }}</el-descriptions-item>
          </template>
        </el-descriptions>
      </el-card>

      <!-- 右侧：用户基本信息卡片 -->
      <el-card class="user-card">
        <template #header>
          <div class="card-header">
            <span>用户基本信息</span>
            <el-button type="primary" @click="handleUserEdit">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
          </div>
        </template>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="ID">{{ userDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="账号">{{ userDetail.account }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleType(userDetail.role)">
              {{ getRoleLabel(userDetail.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
              {{ userDetail.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ parseTime(userDetail.addTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ parseTime(userDetail.updateTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录时间">
            {{ parseTime(userDetail.lastLoginTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录IP">{{ userDetail.lastLoginIp }}</el-descriptions-item>
          <el-descriptions-item label="登录次数">{{ userDetail.loginCount }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="`编辑${getRoleLabel(userDetail.role)}信息`"
      width="50%"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <!-- 管理员编辑表单 -->
        <template v-if="userDetail.role === 'admin'">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" />
          </el-form-item>
        </template>

        <!-- 教师编辑表单 -->
        <template v-if="userDetail.role === 'teacher'">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="工号" prop="teacherNumber">
            <el-input v-model="editForm.teacherNumber" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" />
          </el-form-item>
        </template>

        <!-- 学生编辑表单 -->
        <template v-if="userDetail.role === 'student'">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="学号" prop="studentNumber">
            <el-input v-model="editForm.studentNumber" />
          </el-form-item>
          <el-form-item label="班级" prop="classId">
            <el-select v-model="editForm.classId" placeholder="请选择班级">
              <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" />
          </el-form-item>
        </template>
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

    <!-- 用户基本信息编辑对话框 -->
    <el-dialog
      v-model="userEditDialogVisible"
      title="编辑用户基本信息"
      width="50%"
    >
      <el-form
        ref="userEditFormRef"
        :model="userEditForm"
        :rules="userEditRules"
        label-width="100px"
      >
        <el-form-item label="账号" prop="account">
          <el-input v-model="userEditForm.account" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="userEditForm.password" 
            type="password" 
            show-password 
            placeholder="不修改请留空"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="userEditForm.status"
            size="large"
            inline-prompt
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserEdit" :loading="userEditLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserById, updateUser } from '@/api/user'
import { getTeacherByUserId, updateTeacher } from '@/api/teacher.js'
import { getStudentByUserId, updateStudent} from "@/api/student.js"
import { getAdministratorByUserId, updateAdministrator } from "@/api/administrator.js"
import {getAllClasses} from "@/api/class.js"

import { parseTime } from '@/utils/Utils'
import { ArrowLeft, Edit } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 用户详情数据
const userDetail = reactive({
  id: '',
  account: '',
  role: ''
})
const roleDetail = reactive({})

// 编辑相关
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editLoading = ref(false)
const classOptions = ref([])

const editForm = reactive({
  name: '',
  phone: '',
  email: '',
  teacherNumber: '',
  studentNumber: '',
  classId: ''
})

const editRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  teacherNumber: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

// 用户基本信息编辑相关
const userEditDialogVisible = ref(false)
const userEditFormRef = ref(null)
const userEditLoading = ref(false)

const userEditForm = reactive({
  id: '',
  account: '',
  password: '',
  status: 1
})

const userEditRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取角色标签类型
const getRoleType = (role) => {
  const types = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return types[role] || 'info'
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const labels = {
    'admin': '管理员',
    'teacher': '教师',
    'student': '学生'
  }
  return labels[role] || role
}

// 获取用户详情
async function fetchUserDetail() {
  try {
    const userId = route.params.id
    const res = await getUserById(userId)
    if (res.code === 200) {
      Object.assign(userDetail, res.data)
      
      // 根据角色获取详细信息
      if (userDetail.role === 'admin') {
        const adminRes = await getAdministratorByUserId(userId)
        if (adminRes.code === 200) {
          Object.assign(roleDetail, adminRes.data)
        }
      } else if (userDetail.role === 'teacher') {
        const teacherRes = await getTeacherByUserId(userId)
        if (teacherRes.code === 200) {
          Object.assign(roleDetail, teacherRes.data)
        }
      } else if (userDetail.role === 'student') {
        const studentRes = await getStudentByUserId(userId)
        if (studentRes.code === 200) {
          Object.assign(roleDetail, studentRes.data)
        }
      }
    } else {
      ElMessage.error(res.message || '获取用户详情失败')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 获取班级列表
async function fetchClassList() {
  try {
    const res = await getAllClasses()
    if (res.code === 200) {
      classOptions.value = res.data
    } else {
      ElMessage.error(res.message || '获取班级列表失败')
    }
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 打开编辑对话框
function handleEdit() {
  resetForm()
  editDialogVisible.value = true
}

// 重置表单
function resetForm() {
  for (let key in editForm) {
    delete editForm[key]
  }
}

// 取消操作
function handleCancel() {
  resetForm()
  editDialogVisible.value = false
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {
        // 添加更新时间
        const currentTime = new Date().toISOString()
        const submitData = {
          ...editForm,
          updateTime: currentTime
        }

        let res
        if (userDetail.role === 'admin') {
          res = await updateAdministrator(submitData)
        } else if (userDetail.role === 'teacher') {
          res = await updateTeacher(submitData)
        } else if (userDetail.role === 'student') {
          res = await updateStudent(submitData)
        }

        if (res.code === 200) {
          ElMessage.success('更新成功')
          editDialogVisible.value = false
          // 刷新数据
          await fetchUserDetail()
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      } finally {
        editLoading.value = false
      }
    }
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 打开用户基本信息编辑对话框
const handleUserEdit = () => {
  // 重置表单
  Object.assign(userEditForm, {
    id: userDetail.id,
    account: userDetail.account,
    password: '',
    status: userDetail.status
  })
  userEditDialogVisible.value = true
}

// 提交用户基本信息编辑
const submitUserEdit = async () => {
  if (!userEditFormRef.value) return
  
  await userEditFormRef.value.validate(async (valid) => {
    if (valid) {
      userEditLoading.value = true
      try {
        // 添加更新时间
        const currentTime = new Date().toISOString()
        const submitData = {
          ...userEditForm,
          updateTime: currentTime
        }

        // 如果密码为空，则不更新密码
        if (!submitData.password) {
          delete submitData.password
        }

        const res = await updateUser(submitData)
        if (res.code === 200) {
          ElMessage.success('更新成功')
          userEditDialogVisible.value = false
          // 刷新数据
          await fetchUserDetail()
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      } finally {
        userEditLoading.value = false
      }
    }
  })
}

// 初始化
onMounted(() => {
  fetchUserDetail()
  fetchClassList()
})
</script>

<style scoped>
.user-detail-container {
  padding: 20px;
}

.detail-layout {
  display: flex;
  gap: 20px;
}

.role-card {
  flex: 2;
}

.user-card {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 编辑对话框样式 */
:deep(.el-dialog__body) {
  padding: 20px 40px;
}

:deep(.el-form-item__content) {
  width: 100%;
}

:deep(.el-input),
:deep(.el-select) {
  width: 100%;
}

:deep(.el-switch) {
  margin-left: 0;
}
</style> 