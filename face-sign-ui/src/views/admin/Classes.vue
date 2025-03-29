<template>
  <div class="classes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
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
              添加班级
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="班级编号">
          <el-input v-model="searchForm.classId" placeholder="请输入班级编号" clearable />
        </el-form-item>
        <el-form-item label="班级名称">
          <el-input v-model="searchForm.name" placeholder="请输入班级名称" clearable />
        </el-form-item>
        <el-form-item label="院系">
          <el-select v-model="searchForm.departmentId" placeholder="请选择院系" clearable>
            <el-option
              v-for="item in departmentOptions"
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

      <!-- 班级列表 -->
      <el-table :data="classList" style="width: 100%" v-loading="loading">
        <el-table-column prop="classId" label="班级编号" width="120" />
        <el-table-column prop="name" label="班级名称" width="150" />
        <el-table-column prop="departmentName" label="所属院系" width="200" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="primary" link @click="handleAssignStudents(scope.row)">
              分配学生
            </el-button>
            <el-button type="primary" link @click="handleViewDetails(scope.row)">
              查看详情
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
        <el-form-item label="班级编号" prop="classId">
          <el-input v-model="classForm.classId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="classForm.name" />
        </el-form-item>
        <el-form-item label="所属院系" prop="departmentId">
          <el-select v-model="classForm.departmentId" placeholder="请选择院系">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="classForm.grade" placeholder="请选择年级">
            <el-option label="2024级" value="2024" />
            <el-option label="2023级" value="2023" />
            <el-option label="2022级" value="2022" />
            <el-option label="2021级" value="2021" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="classForm.status"
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

    <!-- 分配学生对话框 -->
    <el-dialog
      v-model="showAssignDialog"
      title="分配学生"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="班级名称">
          <span>{{ currentClass?.name }}</span>
        </el-form-item>
        <el-form-item label="学生">
          <el-select
            v-model="assignForm.studentIds"
            multiple
            filterable
            placeholder="请选择学生"
            style="width: 100%"
          >
            <el-option
              v-for="item in studentOptions"
              :key="item.studentId"
              :label="item.name"
              :value="item.studentId"
            >
              <span>{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ item.studentId }}
              </span>
            </el-option>
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

    <!-- 班级详情对话框 -->
    <el-dialog
      v-model="showDetailsDialog"
      title="班级详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="班级编号">{{ currentClass?.classId }}</el-descriptions-item>
        <el-descriptions-item label="班级名称">{{ currentClass?.name }}</el-descriptions-item>
        <el-descriptions-item label="所属院系">{{ currentClass?.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ currentClass?.grade }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ currentClass?.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentClass?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentClass?.status === 1 ? 'success' : 'danger'">
            {{ currentClass?.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div class="details-section">
        <h3>班级学生列表</h3>
        <el-table :data="classStudents" style="width: 100%">
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="gender" label="性别" width="80">
            <template #default="scope">
              {{ scope.row.gender === 1 ? '男' : '女' }}
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="联系电话" width="150" />
          <el-table-column prop="email" label="邮箱" />
        </el-table>
      </div>
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
  classId: '',
  name: '',
  departmentId: ''
})

// 院系选项
const departmentOptions = ref([])

// 学生选项
const studentOptions = ref([])

// 班级列表数据
const classList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 添加/编辑对话框
const showAddDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const classFormRef = ref(null)
const classForm = reactive({
  classId: '',
  name: '',
  departmentId: '',
  grade: '',
  status: 1
})

// 表单验证规则
const rules = {
  classId: [
    { required: true, message: '请输入班级编号', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '班级编号必须为6位数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入班级名称', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属院系', trigger: 'change' }
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ]
}

// 分配学生对话框
const showAssignDialog = ref(false)
const assigning = ref(false)
const currentClass = ref(null)
const assignForm = reactive({
  studentIds: []
})

// 班级详情对话框
const showDetailsDialog = ref(false)
const classStudents = ref([])

// 导入对话框
const showImportDialog = ref(false)
const uploadHeaders = {
  Authorization: `Bearer ${proxy.$common.getCookies(proxy.$config.tokenKeyName)}`
}

// 获取院系列表
const fetchDepartmentList = async () => {
  try {
    // TODO: 调用获取院系列表API
    departmentOptions.value = [
      { id: 1, name: '计算机科学与技术学院' },
      { id: 2, name: '信息工程学院' }
    ]
  } catch (error) {
    ElMessage.error('获取院系列表失败')
  }
}

// 获取学生列表
const fetchStudentList = async () => {
  try {
    // TODO: 调用获取学生列表API
    studentOptions.value = [
      {
        studentId: '20240001',
        name: '张三',
        gender: 1,
        phone: '13800138000',
        email: 'zhangsan@example.com'
      }
    ]
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  }
}

// 获取班级列表
const fetchClassList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取班级列表API
    classList.value = [
      {
        classId: '2024001',
        name: '计算机科学1班',
        departmentName: '计算机科学与技术学院',
        grade: '2024',
        studentCount: 30,
        createTime: '2024-03-28 10:00:00',
        status: 1
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchClassList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.classId = ''
  searchForm.name = ''
  searchForm.departmentId = ''
  handleSearch()
}

// 添加班级
const handleAdd = () => {
  isEdit.value = false
  Object.assign(classForm, {
    classId: '',
    name: '',
    departmentId: '',
    grade: '',
    status: 1
  })
  showAddDialog.value = true
}

// 编辑班级
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(classForm, row)
  showAddDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!classFormRef.value) return
  await classFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用添加/编辑班级API
        ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
        showAddDialog.value = false
        fetchClassList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除班级
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该班级吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // TODO: 调用删除班级API
      ElMessage.success('删除成功')
      fetchClassList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 分配学生
const handleAssignStudents = (row) => {
  currentClass.value = row
  assignForm.studentIds = row.studentIds || []
  showAssignDialog.value = true
}

// 提交分配学生
const handleAssignSubmit = async () => {
  if (!assignForm.studentIds.length) {
    ElMessage.warning('请选择学生')
    return
  }
  assigning.value = true
  try {
    // TODO: 调用分配学生API
    ElMessage.success('分配学生成功')
    showAssignDialog.value = false
    fetchClassList()
  } catch (error) {
    ElMessage.error('分配学生失败')
  } finally {
    assigning.value = false
  }
}

// 查看详情
const handleViewDetails = async (row) => {
  currentClass.value = row
  try {
    // TODO: 调用获取班级学生列表API
    classStudents.value = [
      {
        studentId: '20240001',
        name: '张三',
        gender: 1,
        phone: '13800138000',
        email: 'zhangsan@example.com'
      }
    ]
    showDetailsDialog.value = true
  } catch (error) {
    ElMessage.error('获取班级详情失败')
  }
}

// 导入班级
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
    fetchClassList()
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
    // TODO: 调用导出班级数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchClassList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchClassList()
}

onMounted(() => {
  fetchDepartmentList()
  fetchStudentList()
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

.details-section {
  margin-top: 20px;
}

.details-section h3 {
  margin-bottom: 15px;
}
</style> 