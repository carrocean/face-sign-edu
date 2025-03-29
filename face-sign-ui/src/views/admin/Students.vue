<template>
  <div class="students-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
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
              添加学生
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentId" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable>
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 学生列表 -->
      <el-table :data="studentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="primary" link @click="handleAssignClass(scope.row)">
              分配班级
            </el-button>
            <el-button 
              type="danger" 
              link 
              @click="handleDelete(scope.row)"
            >
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

    <!-- 添加/编辑学生对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑学生' : '添加学生'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="studentFormRef"
        :model="studentForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="studentForm.studentId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="studentForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="studentForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="studentForm.email" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="studentForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配班级对话框 -->
    <el-dialog
      v-model="showAssignDialog"
      title="分配班级"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="学生姓名">
          <span>{{ currentStudent?.name }}</span>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="assignForm.classId" placeholder="请选择班级">
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAssignDialog = false">取消</el-button>
          <el-button type="primary" @click="handleAssignSubmit" :loading="assigning">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="showImportDialog"
      title="批量导入学生"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-upload
        class="upload-demo"
        drag
        action="/api/face/sign/admin/student/import"
        :headers="uploadHeaders"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Upload,
  Download,
  UploadFilled
} from '@element-plus/icons-vue'
const {proxy} = getCurrentInstance()

// 搜索表单
const searchForm = reactive({
  studentId: '',
  name: '',
  classId: ''
})

// 班级选项
const classOptions = ref([])

// 学生列表数据
const studentList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 添加/编辑对话框
const showAddDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const studentFormRef = ref(null)
const studentForm = reactive({
  studentId: '',
  name: '',
  gender: 1,
  phone: '',
  email: '',
  status: 1
})

// 表单验证规则
const rules = {
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{8}$/, message: '学号必须为8位数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 分配班级对话框
const showAssignDialog = ref(false)
const assigning = ref(false)
const currentStudent = ref(null)
const assignForm = reactive({
  classId: ''
})

// 导入对话框
const showImportDialog = ref(false)
const uploadHeaders = {
  Authorization: `Bearer ${proxy.$common.getCookies(proxy.$config.tokenKeyName)}`
}

// 获取班级列表
const fetchClassList = async () => {
  try {
    // TODO: 调用获取班级列表API
    classOptions.value = [
      { id: 1, name: '计算机科学1班' },
      { id: 2, name: '计算机科学2班' }
    ]
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 获取学生列表
const fetchStudentList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取学生列表API
    studentList.value = [
      {
        studentId: '20240001',
        name: '张三',
        gender: 1,
        className: '计算机科学1班',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        status: 1
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchStudentList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.studentId = ''
  searchForm.name = ''
  searchForm.classId = ''
  handleSearch()
}

// 添加学生
const handleAdd = () => {
  isEdit.value = false
  Object.assign(studentForm, {
    studentId: '',
    name: '',
    gender: 1,
    phone: '',
    email: '',
    status: 1
  })
  showAddDialog.value = true
}

// 编辑学生
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(studentForm, row)
  showAddDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!studentFormRef.value) return
  await studentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用添加/编辑学生API
        ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
        showAddDialog.value = false
        fetchStudentList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除学生
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该学生吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // TODO: 调用删除学生API
      ElMessage.success('删除成功')
      fetchStudentList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 分配班级
const handleAssignClass = (row) => {
  currentStudent.value = row
  assignForm.classId = row.classId
  showAssignDialog.value = true
}

// 提交分配班级
const handleAssignSubmit = async () => {
  if (!assignForm.classId) {
    ElMessage.warning('请选择班级')
    return
  }
  assigning.value = true
  try {
    // TODO: 调用分配班级API
    ElMessage.success('分配班级成功')
    showAssignDialog.value = false
    fetchStudentList()
  } catch (error) {
    ElMessage.error('分配班级失败')
  } finally {
    assigning.value = false
  }
}

// 导入学生
const handleImport = () => {
  showImportDialog.value = true
}

// 上传前验证
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

// 导入成功
const handleImportSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('导入成功')
    showImportDialog.value = false
    fetchStudentList()
  } else {
    ElMessage.error(response.message || '导入失败')
  }
}

// 导入失败
const handleImportError = () => {
  ElMessage.error('导入失败')
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出学生数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchStudentList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchStudentList()
}

onMounted(() => {
  fetchClassList()
  fetchStudentList()
})
</script>

<style scoped>
.students-container {
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