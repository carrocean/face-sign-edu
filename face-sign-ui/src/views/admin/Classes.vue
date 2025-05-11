<template>
  <div class="classes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">
              <el-icon>
                <Plus/>
              </el-icon>
              添加班级
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="班级名称">
          <el-input v-model="searchForm.className" placeholder="请输入班级名称" clearable style="width: 200px"/>
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

      <!-- 班级表格 -->
      <el-table
          :data="classList"
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="className" label="班级名称" align="center"/>
        <el-table-column prop="addTime" label="创建时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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

    <!-- 添加/编辑班级对话框 -->
    <el-dialog
        v-model="showAddDialog"
        :title="isEdit ? '编辑班级' : '添加班级'"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="classFormRef"
          :model="classForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="classForm.className" />
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
        title="批量导入班级"
        width="400px"
        :close-on-click-modal="false"
    >
      <el-upload
          class="upload-demo"
          drag
          action="/api/face/sign/admin/class/import"
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
import {getAllPageClasses, deleteClass, updateClass, saveClass, batchDeleteClasses} from '@/api/class.js'
import router from "@/router/index.js";

const {proxy} = getCurrentInstance()

// 选中的班级
const selectedClasses = ref([])

// 搜索表单
const searchForm = reactive({
  className: ''
})

// 班级列表数据
const classList = ref([])
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
const classFormRef = ref(null)
const classForm = reactive({
  className: ''
})

// 表单验证规则
const rules = {
  className: [
    {required: true, message: '请输入班级名称', trigger: 'blur'}
  ]
}

// 获取班级列表
async function fetchClassList() {
  try {
    loading.value = true
    const res = await getAllPageClasses(pageParams, searchForm)
    if (res.code === 200) {
      classList.value = res.data.records
      pageParams.total = res.data.total
    } else {
      ElMessage.error(res.message || '获取班级列表失败')
    }
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchClassList()
}

// 重置搜索
function resetSearch() {
  searchForm.className = ''
  pageParams.fuzzySearch = true  // 重置时保持模糊查询开启
  handleSearch()
}

// 添加班级
function handleAdd() {
  isEdit.value = false
  resetForm()
  showAddDialog.value = true
}

// 编辑班级
function handleEdit(row) {
  isEdit.value = true
  Object.assign(classForm, row)
  showAddDialog.value = true
}

// 重置表单
function resetForm() {
  for (let key in classForm) {
    delete classForm[key]
  }
}

// 取消操作
function handleCancel() {
  resetForm()
  showAddDialog.value = false
}

// 提交表单
function handleSubmit() {
  classFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true
      if (isEdit.value) {
        // 编辑班级
        updateClass(classForm).then(res => {
          if (res.code === 200) {
            ElMessage.success('编辑成功')
            showAddDialog.value = false
            fetchClassList()
          } else {
            ElMessage.error(res.message || '编辑失败')
          }
        }).catch(err => {
          console.error('编辑班级失败:', err)
          ElMessage.error('编辑失败')
        }).finally(() => {
          submitting.value = false
        })
      } else {
        // 添加班级
        saveClass(classForm).then(res => {
          if (res.code === 200) {
            ElMessage.success('添加成功')
            showAddDialog.value = false
            fetchClassList()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        }).catch(err => {
          console.error('添加班级失败:', err)
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

// 删除班级
function handleDelete(row) {
  ElMessageBox.confirm('确认删除该班级吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => { // TODO
    ElMessage.error('删除失败，所在班级还有关联的学生')
    // try {
    //   const res = await deleteClass(row.id)
    //   if (res.code === 200) {
    //     ElMessage.success('删除成功')
    //     await fetchClassList()
    //   } else {
    //     ElMessage.error(res.message || '删除失败')
    //   }
    // } catch (error) {
    //   console.error('删除班级失败:', error)
    //   ElMessage.error('删除失败')
    // }
  })
}

// 处理选择变化
function handleSelectionChange(selection) {
  selectedClasses.value = selection
}

// 批量删除
function handleBatchDelete() {
  if (selectedClasses.value.length === 0) {
    ElMessage.warning('请选择要删除的班级')
    return
  }

  ElMessageBox.confirm(`确认删除选中的 ${selectedClasses.value.length} 个班级吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => { // TODO
    ElMessage.error('删除失败，所在班级还有关联的学生')
    // try {
    //   const ids = selectedClasses.value.map(cls => cls.id)
    //   const res = await batchDeleteClasses(ids)
    //   if (res.code === 200) {
    //     ElMessage.success('批量删除成功')
    //     await fetchClassList()
    //   } else {
    //     ElMessage.error(res.message || '批量删除失败')
    //   }
    // } catch (error) {
    //   console.error('批量删除班级失败:', error)
    //   ElMessage.error('批量删除失败')
    // }
  })
}

// 分页相关方法
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchClassList()
}

function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchClassList()
}

// 导入对话框
const showImportDialog = ref(false)
const uploadHeaders = {
  Authorization: `Bearer ${proxy.$common.getCookies(proxy.$config.tokenKeyName)}`
}

// 导入班级
function handleImport() {
  showImportDialog.value = true
}

// 导入成功
function handleImportSuccess(response) {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    showImportDialog.value = false
    fetchClassList()
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

onMounted(() => {
  fetchClassList()
})
</script>

<style scoped>
.classes-container {
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