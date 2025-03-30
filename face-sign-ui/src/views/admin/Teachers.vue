<template>
  <div class="teachers-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
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
              添加教师
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工号">
          <el-input v-model="searchForm.teacherId" placeholder="请输入工号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
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

      <!-- 教师列表 -->
      <el-table :data="teacherList" style="width: 100%" v-loading="loading">
        <el-table-column prop="teacherId" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="院系" width="150" />
        <el-table-column prop="title" label="职称" width="120" />
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
            <el-button type="primary" link @click="handleAssignCourse(scope.row)">
              分配课程
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

    <!-- 添加/编辑教师对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEdit ? '编辑教师' : '添加教师'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="teacherFormRef"
        :model="teacherForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="工号" prop="teacherId">
          <el-input v-model="teacherForm.teacherId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacherForm.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="teacherForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="院系" prop="departmentId">
          <el-select v-model="teacherForm.departmentId" placeholder="请选择院系">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="teacherForm.title" placeholder="请选择职称">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="teacherForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="teacherForm.email" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="teacherForm.status"
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

    <!-- 分配课程对话框 -->
    <el-dialog
      v-model="showAssignDialog"
      title="分配课程"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="教师姓名">
          <span>{{ currentTeacher?.name }}</span>
        </el-form-item>
        <el-form-item label="课程">
          <el-select
            v-model="assignForm.courseIds"
            multiple
            placeholder="请选择课程"
            style="width: 100%"
          >
            <el-option
              v-for="item in courseOptions"
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
      title="批量导入教师"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-upload
        class="upload-demo"
        drag
        action="/api/face/sign/admin/teacher/import"
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
  teacherId: '',
  name: '',
  departmentId: ''
})

// 院系选项
const departmentOptions = ref([])

// 课程选项
const courseOptions = ref([])

// 教师列表数据
const teacherList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 添加/编辑对话框
const showAddDialog = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const teacherFormRef = ref(null)
const teacherForm = reactive({
  teacherId: '',
  name: '',
  gender: 1,
  departmentId: '',
  title: '',
  phone: '',
  email: '',
  status: 1
})

// 表单验证规则
const rules = {
  teacherId: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '工号必须为6位数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 分配课程对话框
const showAssignDialog = ref(false)
const assigning = ref(false)
const currentTeacher = ref(null)
const assignForm = reactive({
  courseIds: []
})

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

// 获取课程列表
const fetchCourseList = async () => {
  try {
    // TODO: 调用获取课程列表API
    courseOptions.value = [
      { id: 1, name: '高等数学' },
      { id: 2, name: '大学物理' },
      { id: 3, name: '程序设计' }
    ]
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

// 获取教师列表
async function fetchTeacherList() {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const res = await getAllTeachers(params, searchForm)
    if (res.code === 200) {
      teacherList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取教师列表失败')
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchTeacherList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.teacherId = ''
  searchForm.name = ''
  searchForm.departmentId = ''
  handleSearch()
}

// 添加教师
const handleAdd = () => {
  isEdit.value = false
  Object.assign(teacherForm, {
    teacherId: '',
    name: '',
    gender: 1,
    departmentId: '',
    title: '',
    phone: '',
    email: '',
    status: 1
  })
  showAddDialog.value = true
}

// 编辑教师
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(teacherForm, row)
  showAddDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!teacherFormRef.value) return
  await teacherFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用添加/编辑教师API
        ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
        showAddDialog.value = false
        fetchTeacherList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除教师
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该教师吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // TODO: 调用删除教师API
      ElMessage.success('删除成功')
      fetchTeacherList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 分配课程
const handleAssignCourse = (row) => {
  currentTeacher.value = row
  assignForm.courseIds = row.courseIds || []
  showAssignDialog.value = true
}

// 提交分配课程
const handleAssignSubmit = async () => {
  if (!assignForm.courseIds.length) {
    ElMessage.warning('请选择课程')
    return
  }
  assigning.value = true
  try {
    // TODO: 调用分配课程API
    ElMessage.success('分配课程成功')
    showAssignDialog.value = false
    fetchTeacherList()
  } catch (error) {
    ElMessage.error('分配课程失败')
  } finally {
    assigning.value = false
  }
}

// 导入教师
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
    fetchTeacherList()
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
    // TODO: 调用导出教师数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchTeacherList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchTeacherList()
}

onMounted(() => {
  fetchDepartmentList()
  fetchCourseList()
  fetchTeacherList()
})
</script>

<style scoped>
.teachers-container {
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