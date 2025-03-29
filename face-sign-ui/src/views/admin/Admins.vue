<template>
  <div class="admins-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加管理员
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="管理员ID">
          <el-input v-model="searchForm.adminId" placeholder="请输入管理员ID" />
        </el-form-item>
        <el-form-item label="管理员姓名">
          <el-input v-model="searchForm.name" placeholder="请输入管理员姓名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 管理员列表 -->
      <el-table
        v-loading="loading"
        :data="adminList"
        style="width: 100%"
        border
      >
        <el-table-column prop="adminId" label="管理员ID" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ scope.row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="success" link @click="handleResetPassword(scope.row)">
              重置密码
            </el-button>
            <el-button
              :type="scope.row.status === 'ENABLED' ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 'ENABLED' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              删除
            </el-button>
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

    <!-- 添加/编辑管理员对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="dialogType === 'add' ? '添加管理员' : '编辑管理员'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="adminFormRef"
        :model="adminForm"
        :rules="adminRules"
        label-width="100px"
      >
        <el-form-item label="管理员ID" prop="adminId">
          <el-input
            v-model="adminForm.adminId"
            placeholder="请输入管理员ID"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="adminForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input
            v-model="adminForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="dialogType === 'add'">
          <el-input
            v-model="adminForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="adminForm.status"
            :active-value="'ENABLED'"
            :inactive-value="'DISABLED'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  adminId: '',
  name: '',
  status: ''
})

// 管理员列表数据
const loading = ref(false)
const adminList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const showDialog = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const adminFormRef = ref(null)

// 管理员表单
const adminForm = reactive({
  adminId: '',
  name: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  status: 'ENABLED'
})

// 表单验证规则
const adminRules = {
  adminId: [
    { required: true, message: '请输入管理员ID', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== adminForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取管理员列表
const fetchAdmins = async () => {
  loading.value = true
  try {
    // TODO: 调用获取管理员列表API
    adminList.value = [
      {
        adminId: 'admin001',
        name: '管理员',
        phone: '13800138000',
        email: 'admin@example.com',
        lastLoginTime: '2024-03-28 10:00:00',
        status: 'ENABLED'
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取管理员列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAdmins()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    adminId: '',
    name: '',
    status: ''
  })
  handleSearch()
}

// 添加管理员
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(adminForm, {
    adminId: '',
    name: '',
    phone: '',
    email: '',
    password: '',
    confirmPassword: '',
    status: 'ENABLED'
  })
  showDialog.value = true
}

// 编辑管理员
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(adminForm, {
    adminId: row.adminId,
    name: row.name,
    phone: row.phone,
    email: row.email,
    status: row.status
  })
  showDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!adminFormRef.value) return
  await adminFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用添加/编辑管理员API
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '编辑成功')
        showDialog.value = false
        fetchAdmins()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '编辑失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要重置该管理员的密码吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用重置密码API
    ElMessage.success('重置密码成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败')
    }
  }
}

// 切换状态
const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 'ENABLED' ? '禁用' : '启用'}该管理员吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用更新状态API
    ElMessage.success(`${row.status === 'ENABLED' ? '禁用' : '启用'}成功`)
    fetchAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${row.status === 'ENABLED' ? '禁用' : '启用'}失败`)
    }
  }
}

// 删除管理员
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该管理员吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用删除管理员API
    ElMessage.success('删除成功')
    fetchAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAdmins()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAdmins()
}

onMounted(() => {
  fetchAdmins()
})
</script>

<style scoped>
.admins-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 