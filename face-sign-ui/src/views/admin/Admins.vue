<template>
  <div class="admins-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <div class="header-buttons">
            <el-button
                v-if="selectedAdmins.length > 0"
                type="danger"
                @click="handleBatchDelete"
            >
              <el-icon>
                <Delete/>
              </el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleImport">
              <el-icon>
                <Upload/>
              </el-icon>
              批量导入
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon>
                <Download/>
              </el-icon>
              导出数据
            </el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon>
                <Plus/>
              </el-icon>
              添加管理员
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable style="width: 200px"/>
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

      <!-- 管理员表格 -->
      <el-table
          :data="adminList"
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center"/>
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="name" label="姓名" align="center"/>
        <el-table-column prop="phone" label="联系电话" align="center"/>
        <el-table-column prop="email" label="邮箱" align="center"/>
        <el-table-column prop="addTime" label="创建时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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

    <!-- 添加/编辑管理员对话框 -->
    <el-dialog
        v-model="showAddDialog"
        :title="isEdit ? '编辑管理员' : '添加管理员'"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="adminFormRef"
          :model="adminForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="adminForm.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="adminForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
        v-model="showImportDialog"
        title="批量导入管理员"
        width="400px"
        :close-on-click-modal="false"
    >
      <el-upload
          class="upload-demo"
          drag
          action="/api/face/sign/admin/administrator/import"
          :headers="uploadHeaders"
          :on-success="handleImportSuccess"
          :on-error="handleImportError"
          :before-upload="beforeUpload"
      >
        <el-icon class="el-icon--upload">
          <upload-filled/>
        </el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx 文件，且文件大小不超过 10MB
          </div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, getCurrentInstance} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Upload,
  Download,
  UploadFilled,
  Delete
} from '@element-plus/icons-vue'
import {parseTime} from '@/utils/Utils'
import {getAllPageAdministrators, deleteAdministrator, updateAdministrator, addAdministrator, batchDeleteAdministrators} from '@/api/administrator.js'
import router from "@/router/index.js";

const {proxy} = getCurrentInstance()

// 选中的管理员
const selectedAdmins = ref([])

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 管理员列表数据
const adminList = ref([])
const loading = ref(false)
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

// 添加/编辑对话框
const showAddDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const adminFormRef = ref(null)
const adminForm = reactive({
  name: '',
  phone: '',
  email: ''
})

// 表单验证规则
const rules = {
  name: [
    {required: true, message: '请输入姓名', trigger: 'blur'}
  ],
  phone: [
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur'}
  ],
  email: [
    {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}
  ]
}

// 获取管理员列表
async function fetchAdminList() {
  try {
    loading.value = true
    const res = await getAllPageAdministrators(pageParams, searchForm)
    if (res.code === 200) {
      adminList.value = res.data.records
      pageParams.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取管理员列表失败')
    }
  } catch (error) {
    ElMessage.error('获取管理员列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchAdminList()
}

// 重置搜索
function resetSearch() {
  searchForm.name = ''
  pageParams.fuzzySearch = true  // 重置时保持模糊查询开启
  handleSearch()
}

// 添加管理员
function handleAdd() {
  isEdit.value = false
  resetForm()
  showAddDialog.value = true
}

// 编辑管理员
function handleEdit(row) {
  isEdit.value = true
  Object.assign(adminForm, row)
  showAddDialog.value = true
}

// 重置表单
function resetForm() {
  for (let key in adminForm) {
    delete adminForm[key]
  }
}

// 取消操作
function handleCancel() {
  resetForm()
  showAddDialog.value = false
}

// 提交表单
function handleSubmit() {
  adminFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true
      if (isEdit.value) {
        // 编辑管理员
        updateAdministrator(adminForm).then(res => {
          if (res.code === 200) {
            ElMessage.success('编辑成功')
            showAddDialog.value = false
            fetchAdminList()
          } else {
            ElMessage.error(res.message || '编辑失败')
          }
        }).catch(err => {
          console.error('编辑管理员失败:', err)
          ElMessage.error('编辑失败')
        }).finally(() => {
          submitting.value = false
        })
      } else {
        // 添加管理员
        addAdministrator(adminForm).then(res => {
          if (res.code === 200) {
            ElMessage.success('添加成功')
            showAddDialog.value = false
            fetchAdminList()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        }).catch(err => {
          console.error('添加管理员失败:', err)
          ElMessage.error('添加失败')
        }).finally(() => {
          submitting.value = false
        })
      }
    } else {
      ElMessage.error('请补全信息')
    }
  })
}

// 删除管理员
function handleDelete(row) {
  ElMessageBox.confirm('确认删除该管理员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteAdministrator(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        await fetchAdminList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除管理员失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 处理选择变化
function handleSelectionChange(selection) {
  selectedAdmins.value = selection
}

// 批量删除
function handleBatchDelete() {
  if (selectedAdmins.value.length === 0) {
    ElMessage.warning('请选择要删除的管理员')
    return
  }

  ElMessageBox.confirm(`确认删除选中的 ${selectedAdmins.value.length} 个管理员吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedAdmins.value.map(admin => admin.id)
      const res = await batchDeleteAdministrators(ids)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        await fetchAdminList()
      } else {
        ElMessage.error(res.message || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除管理员失败:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

// 分页相关方法
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchAdminList()
}

function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchAdminList()
}

// 导入对话框
const showImportDialog = ref(false)
const uploadHeaders = {
  Authorization: `Bearer ${proxy.$common.getCookies(proxy.$config.tokenKeyName)}`
}

// 导入管理员
function handleImport() {
  showImportDialog.value = true
}

// 导入成功
function handleImportSuccess(response) {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    showImportDialog.value = false
    fetchAdminList()
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

// 导入失败
function handleImportError() {
  ElMessage.error('导入失败')
}

// 导出数据
async function handleExport() {
  try {
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 查看详情
function handleViewDetail(row) {
  let id = row.userId
  router.push(`/admin/users/${id}`)
}

onMounted(() => {
  fetchAdminList()
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

.header-buttons {
  display: flex;
  gap: 10px;
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

.upload-demo {
  text-align: center;
}

.el-upload__tip {
  margin-top: 10px;
  color: #909399;
}
</style> 