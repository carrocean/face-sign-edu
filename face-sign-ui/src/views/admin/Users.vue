<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-buttons">
            <el-button
                v-if="selectedUsers.length > 0"
                type="danger"
                @click="handleBatchDelete"
            >
              <el-icon>
                <Delete/>
              </el-icon>
              批量删除
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon>
                <Download/>
              </el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="账号">
          <el-input v-model="searchForm.account" placeholder="请输入账号" style="width: 200px"/>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" style="width: 200px">
            <el-option label="管理员" value="ADMIN"/>
            <el-option label="教师" value="TEACHER"/>
            <el-option label="学生" value="STUDENT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 200px">
            <el-option label="启用" :value="1"/>
            <el-option label="禁用" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-switch
              v-model="pageParams.fuzzySearch"
              size="large"
              inline-prompt
              active-text="模糊查询"
              inactive-text="精准查询"
              :active-value="true"
              :inactive-value="false"
              style="margin-left: 10px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 用户表格 -->
      <el-table
          :data="userList"
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center"/>
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="account" label="账号" align="center"/>
        <el-table-column prop="role" label="角色" align="center">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleLabel(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.lastLoginTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginIp" label="最后登录IP" align="center"/>
        <el-table-column prop="loginCount" label="登录次数" align="center"/>
        <el-table-column prop="addTime" label="创建时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
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
            v-model:current-page="pageParams.currentPage"
            v-model:page-size="pageParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pageParams.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {getAllUsers, updateUser, deleteUser, exportUsers, batchDeleteUsers} from '@/api/user'
import {parseTime} from '@/utils/Utils'
import {Download, Delete} from "@element-plus/icons-vue";
import router from "@/router/index.js";

// 选中的用户
const selectedUsers = ref([])

// 搜索表单
const searchForm = reactive({
  account: '',
  role: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const userList = ref([])
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

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

// 搜索
function handleSearch() {
  fetchUserList()
}

// 重置搜索
function resetSearch() {
  searchForm.account = ''
  searchForm.role = ''
  searchForm.status = ''
  pageParams.fuzzySearch = true  // 重置时保持模糊查询开启
  handleSearch()
}

// 获取用户列表
async function fetchUserList() {
  try {
    loading.value = true

    const res = await getAllUsers(pageParams, searchForm)
    if (res.code === 200) {
      userList.value = res.data.records
      pageParams.total = res.data.total
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

// 删除用户
function handleDelete(row) {
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

// 处理选择变化
function handleSelectionChange(selection) {
  selectedUsers.value = selection
}

// 批量删除
function handleBatchDelete() {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  ElMessageBox.confirm(`确认删除选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedUsers.value.map(user => user.id)
      const res = await batchDeleteUsers(ids)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        fetchUserList()
      } else {
        ElMessage.error(res.message || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除用户失败:', error)
      ElMessage.error('批量删除失败')
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

// 分页相关方法
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchUserList()
}

function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchUserList()
}

// 导出用户
const handleExport = async () => {
  try {
    const res = await exportUsers()
    if (res.code === 200) {
      // 创建下载链接
      const blob = new Blob([res.data], {type: 'application/vnd.ms-excel'})
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

// 查看详情
const handleViewDetail = (row) => {
  router.push(`/admin/users/${row.id}`)
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


.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  flex: 1;
  margin-bottom: 0;
}

.search-form :deep(.el-form-item) {
  margin-right: 20px;
  margin-bottom: 0;
}

.search-form :deep(.el-form-item__label) {
  width: 60px;
  text-align: right;
}

.search-form :deep(.el-input__wrapper) {
  width: 100%;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style> 