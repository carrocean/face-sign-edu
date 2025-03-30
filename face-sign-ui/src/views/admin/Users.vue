<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="账号">
          <el-input v-model="searchForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.addTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户表格 -->
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="account" label="账号" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleLabel(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间">
          <template #default="scope">
            {{ parseTime(scope.row.lastLoginTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginIp" label="最后登录IP" />
        <el-table-column prop="loginCount" label="登录次数" width="100" />
        <el-table-column prop="addTime" label="创建时间">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'danger' : 'success'" 
              link 
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="账号" prop="account">
          <el-input v-model="userForm.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllUsers, saveUser, updateUser, deleteUser, importUsers, exportUsers, downloadUserTemplate } from '@/api/user'
import { parseTime } from '@/utils/Utils'
import {Download, Plus, Upload} from "@element-plus/icons-vue";

// 搜索表单
const searchForm = reactive({
  account: '',
  role: '',
  status: '',
  addTime: []
})

// 表格数据
const loading = ref(false)
const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const userFormRef = ref(null)
const userForm = reactive({
  account: '',
  name: '',
  role: '',
  email: '',
  password: ''
})

// 表单验证规则
const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 50, message: '账号长度在3-50个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 50, message: '邮箱长度不能超过50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度在6-100个字符之间', trigger: 'blur' }
  ]
}

// 获取角色标签类型
const getRoleType = (role) => {
  const types = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return types[role] || 'info'
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const labels = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return labels[role] || role
}

// 搜索
const handleSearch = () => {
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.account = ''
  searchForm.role = ''
  searchForm.status = ''
  searchForm.addTime = []
  handleSearch()
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    loading.value = true
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    const res = await getAllUsers(params, searchForm)
    if (res.code === 200) {
      userList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(userForm, {
    account: '',
    name: '',
    role: '',
    email: '',
    password: ''
  })
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(userForm, row)
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteUser(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchUserList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 切换用户状态
const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updateUser({
        ...row,
        status: row.status === 1 ? 0 : 1
      })
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        fetchUserList()
      } else {
        ElMessage.error(res.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}用户失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  })
}

// 提交表单
const handleSubmit = () => {
  userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = dialogType.value === 'add' ? saveUser : updateUser
        const res = await api(userForm)
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          fetchUserList()
        } else {
          ElMessage.error(res.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
        }
      } catch (error) {
        console.error('保存用户失败:', error)
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 导入前校验
const beforeImport = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                  file.type === 'application/vnd.ms-excel'
  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('文件大小不能超过 2MB!')
    return false
  }
  return true
}

// 批量导入
const handleImport = () => {
  // 创建一个隐藏的文件输入框
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    // 验证文件
    if (!beforeImport(file)) return
    
    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await importUsers(formData)
      if (res.code === 200) {
        ElMessage.success('导入成功')
        fetchUserList()
      } else {
        ElMessage.error(res.message || '导入失败')
      }
    } catch (error) {
      console.error('导入用户失败:', error)
      ElMessage.error('导入失败')
    }
  }
  input.click()
}

// 导出用户
const handleExport = async () => {
  try {
    const res = await exportUsers()
    if (res.code === 200) {
      // 创建下载链接
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = `用户列表_${parseTime(new Date(), '{y}{m}{d}')}.xlsx`
      link.click()
      window.URL.revokeObjectURL(link.href)
      ElMessage.success('导出成功')
    } else {
      ElMessage.error(res.message || '导出失败')
    }
  } catch (error) {
    console.error('导出用户失败:', error)
    ElMessage.error('导出失败')
  }
}

// 下载模板
const downloadTemplate = async () => {
  try {
    const res = await downloadUserTemplate()
    if (res.code === 200) {
      // 创建下载链接
      const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = '用户导入模板.xlsx'
      link.click()
      window.URL.revokeObjectURL(link.href)
      ElMessage.success('模板下载成功')
    } else {
      ElMessage.error(res.message || '模板下载失败')
    }
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('模板下载失败')
  }
}

// 初始化
fetchUserList()
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.upload-btn {
  display: inline-block;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 