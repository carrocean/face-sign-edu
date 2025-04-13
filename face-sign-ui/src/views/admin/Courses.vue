<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <div class="header-buttons">
            <el-button
                v-if="selectedCourses.length > 0"
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
              添加课程
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="searchForm.teacherId" placeholder="请选择教师" clearable style="width: 200px">
            <el-option
                v-for="item in teacherOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable style="width: 200px">
            <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="searchForm.semester" placeholder="请输入学期" clearable style="width: 200px"/>
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

      <!-- 课程表格 -->
      <el-table
          :data="courseList"
          style="width: 100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center"/>
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="courseName" label="课程名称" align="center"/>
        <el-table-column prop="teacherId" label="授课教师" align="center">
          <template #default="scope">
            {{ getTeacherName(scope.row.teacherId) }}
          </template>
        </el-table-column>
        <el-table-column prop="classId" label="班级" align="center">
          <template #default="scope">
            {{ getClassName(scope.row.classId) }}
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" align="center"/>
        <el-table-column label="周数" align="center">
          <template #default="scope">
            {{ scope.row.startWeek }}-{{ scope.row.endWeek }}周
          </template>
        </el-table-column>
        <el-table-column prop="addTime" label="创建时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewSchedule(scope.row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            :current-page="pageParams.currentPage"
            :page-size="pageParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pageParams.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            @update:current-page="val => pageParams.currentPage = val"
            @update:page-size="val => pageParams.pageSize = val"
        />
      </div>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
        v-model="showAddDialog"
        :title="isEdit ? '编辑课程' : '添加课程'"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="courseFormRef"
          :model="courseForm"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName"/>
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师">
            <el-option
                v-for="item in teacherOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="courseForm.classId" placeholder="请选择班级">
            <el-option
                v-for="item in classOptions"
                :key="item.id"
                :label="item.className"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="courseForm.semester"/>
        </el-form-item>
        <el-form-item label="起始周" prop="startWeek">
          <el-input-number v-model="courseForm.startWeek" :min="1" :max="20"/>
        </el-form-item>
        <el-form-item label="结束周" prop="endWeek">
          <el-input-number v-model="courseForm.endWeek" :min="1" :max="20"/>
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
        title="批量导入课程"
        width="400px"
        :close-on-click-modal="false"
    >
      <el-upload
          class="upload-demo"
          drag
          action="/api/face/sign/admin/course/import"
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
import {getAllClasses} from '@/api/class.js'
import {getAllTeachers} from '@/api/teacher.js'
import {
  getAllPageCourses,
  saveCourse,
  updateCourse,
  deleteCourse,
  batchDeleteCourses,
  exportCourses
} from '@/api/course.js'
import router from "@/router/index.js";

const {proxy} = getCurrentInstance()

// 选中的课程
const selectedCourses = ref([])

// 搜索表单
const searchForm = reactive({
  courseName: '',
  teacherId: '',
  classId: '',
  semester: ''
})

// 班级和教师选项
const classOptions = ref([])
const teacherOptions = ref([])

// 课程列表数据
const courseList = ref([])
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
const courseFormRef = ref(null)
const courseForm = reactive({
  courseName: '',
  teacherId: '',
  classId: '',
  semester: '',
  startWeek: 1,
  endWeek: 20
})

// 导入对话框
const showImportDialog = ref(false)
const uploadHeaders = reactive({
  Authorization: localStorage.getItem('token')
})

// 表单验证规则
const rules = {
  courseName: [
    {required: true, message: '请输入课程名称', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  teacherId: [
    {required: true, message: '请选择授课教师', trigger: 'change'}
  ],
  classId: [
    {required: true, message: '请选择班级', trigger: 'change'}
  ],
  semester: [
    {required: true, message: '请输入学期', trigger: 'blur'}
  ],
  startWeek: [
    {required: true, message: '请输入起始周', trigger: 'blur'},
    {type: 'number', min: 1, max: 20, message: '周数在 1 到 20 之间', trigger: 'blur'}
  ],
  endWeek: [
    {required: true, message: '请输入结束周', trigger: 'blur'},
    {type: 'number', min: 1, max: 20, message: '周数在 1 到 20 之间', trigger: 'blur'}
  ]
}

// 初始化数据
onMounted(async () => {
  await Promise.all([
    fetchClassOptions(),
    fetchTeacherOptions(),
    fetchCourseList()
  ])
})

// 获取班级选项
async function fetchClassOptions() {
  try {
    const res = await getAllClasses()
    classOptions.value = res.data
  } catch (error) {
    console.error('获取班级列表失败:', error)
  }
}

// 获取教师选项
async function fetchTeacherOptions() {
  try {
    const res = await getAllTeachers()
    teacherOptions.value = res.data
  } catch (error) {
    console.error('获取教师列表失败:', error)
  }
}

// 获取课程列表
async function fetchCourseList() {
  loading.value = true
  try {
    const res = await getAllPageCourses(pageParams, searchForm)
    courseList.value = res.data.records
    pageParams.total = res.data.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取教师名称
function getTeacherName(teacherId) {
  const teacher = teacherOptions.value.find(item => item.id === teacherId)
  return teacher ? teacher.name : ''
}

// 获取班级名称
function getClassName(classId) {
  const classItem = classOptions.value.find(item => item.id === classId)
  return classItem ? classItem.className : ''
}

// 处理表格选择
function handleSelectionChange(selection) {
  selectedCourses.value = selection
}

// 处理搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchCourseList()
}

// 重置搜索
function resetSearch() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pageParams.currentPage = 1
  pageParams.fuzzySearch = true
  fetchCourseList()
}

// 处理分页大小变化
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchCourseList()
}

// 处理页码变化
function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchCourseList()
}

// 处理添加
function handleAdd() {
  isEdit.value = false
  Object.keys(courseForm).forEach(key => {
    courseForm[key] = key === 'startWeek' ? 1 : key === 'endWeek' ? 20 : ''
  })
  showAddDialog.value = true
}

// 处理编辑
function handleEdit(row) {
  isEdit.value = true
  Object.assign(courseForm, row)
  showAddDialog.value = true
}

// 处理删除
function handleDelete(row) {
  ElMessageBox.confirm(
      `确定要删除课程"${row.courseName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await deleteCourse(row.id)
      ElMessage.success('删除成功')
      await fetchCourseList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 处理批量删除
function handleBatchDelete() {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请选择要删除的课程')
    return
  }
  
  ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCourses.value.length} 个课程吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      const ids = selectedCourses.value.map(item => item.id)
      await batchDeleteCourses(ids)
      ElMessage.success('批量删除成功')
      await fetchCourseList()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  })
}

// 处理提交
async function handleSubmit() {
  if (!courseFormRef.value) return
  
  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateCourse(courseForm)
          ElMessage.success('更新成功')
        } else {
          await saveCourse(courseForm)
          ElMessage.success('添加成功')
        }
        showAddDialog.value = false
        await fetchCourseList()
      } catch (error) {
        console.error(isEdit.value ? '更新失败:' : '添加失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
function resetForm() {
  for (let key in courseForm) {
    delete courseForm[key]
  }
}

// 处理取消
function handleCancel() {
  resetForm()
  showAddDialog.value = false
}

// 处理导入 TODO
function handleImport() {
  showImportDialog.value = true
}

// 处理导出 TODO
async function handleExport() {
  try {
    const params = {
      ...searchForm,
      fuzzySearch: pageParams.fuzzySearch
    }
    await exportCourses(params)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 处理查看课程安排
function handleViewSchedule(row) {
  router.push({
    name: 'CourseSchedule',
    params: { id: row.id }
  })
}

// 上传相关方法  TODO
function beforeUpload(file) {
  const isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isXLSX) {
    ElMessage.error('只能上传 xlsx 格式的文件!')
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
  }
  return isXLSX && isLt10M
}

function handleImportSuccess(response) {
  ElMessage.success('导入成功')
  showImportDialog.value = false
  fetchCourseList()
}

function handleImportError() {
  ElMessage.error('导入失败')
}
</script>

<style scoped>
.courses-container {
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

:deep(.el-upload-dragger) {
  width: 100%;
}
</style> 