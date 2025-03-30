<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </template>

      <!-- 课程统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>总课程数</span>
                <el-icon><Reading /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.totalCourses }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>总学生数</span>
                <el-icon><User /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.totalStudents }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>平均出勤率</span>
                <el-icon><DataLine /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.averageAttendance }}%</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="statistics-header">
                <span>本周课程数</span>
                <el-icon><Calendar /></el-icon>
              </div>
            </template>
            <div class="statistics-value">{{ statistics.weeklyCourses }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="ENDED" />
            <el-option label="未开始" value="NOT_STARTED" />
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

      <!-- 课程列表 -->
      <el-table
        v-loading="loading"
        :data="courseList"
        style="width: 100%"
        border
      >
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="attendanceRate" label="出勤率" width="100">
          <template #default="scope">
            {{ scope.row.attendanceRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewStudents(scope.row)">
              学生名单
            </el-button>
            <el-button type="success" link @click="handleViewAttendance(scope.row)">
              考勤记录
            </el-button>
            <el-button type="warning" link @click="handleEdit(scope.row)">
              编辑
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

    <!-- 学生名单对话框 -->
    <el-dialog
      v-model="showStudentsDialog"
      title="学生名单"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-table :data="studentList" style="width: 100%" border>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="attendanceCount" label="出勤次数" width="100" />
        <el-table-column prop="absenceCount" label="缺勤次数" width="100" />
        <el-table-column prop="attendanceRate" label="出勤率" width="100">
          <template #default="scope">
            {{ scope.row.attendanceRate }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewStudentDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="studentCurrentPage"
          v-model:page-size="studentPageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="studentTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleStudentSizeChange"
          @current-change="handleStudentCurrentChange"
        />
      </div>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑课程"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程代码" prop="code">
          <el-input v-model="courseForm.code" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="courseForm.semester" placeholder="请选择学期">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-time-picker
            v-model="courseForm.startTime"
            placeholder="开始时间"
            format="HH:mm"
            value-format="HH:mm"
          />
          <span class="mx-2">至</span>
          <el-time-picker
            v-model="courseForm.endTime"
            placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="courseForm.location" placeholder="请输入上课地点" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitEdit" :loading="submitting">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Download,
  Search,
  Refresh,
  Reading,
  User,
  DataLine,
  Calendar
} from '@element-plus/icons-vue'

// 统计数据
const statistics = reactive({
  totalCourses: 0,
  totalStudents: 0,
  averageAttendance: 0,
  weeklyCourses: 0
})

// 搜索表单
const searchForm = reactive({
  name: '',
  semester: '',
  status: ''
})

// 课程列表数据
const loading = ref(false)
const courseList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 学生名单对话框
const showStudentsDialog = ref(false)
const studentList = ref([])
const studentCurrentPage = ref(1)
const studentPageSize = ref(10)
const studentTotal = ref(0)

// 编辑课程对话框
const showEditDialog = ref(false)
const submitting = ref(false)
const courseFormRef = ref(null)
const courseForm = reactive({
  name: '',
  code: '',
  semester: '',
  startTime: '',
  endTime: '',
  location: '',
  description: ''
})

// 表单验证规则
const courseRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入课程代码', trigger: 'blur' },
    { pattern: /^[A-Z0-9]{6,8}$/, message: '课程代码格式不正确', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
  ]
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // TODO: 调用获取统计数据API
    Object.assign(statistics, {
      totalCourses: 5,
      totalStudents: 150,
      averageAttendance: 95,
      weeklyCourses: 3
    })
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取课程列表
async function fetchCourseList() {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const res = await getAllCourses(params, searchForm)
    if (res.code === 200) {
      courseList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 获取学生名单
const fetchStudentList = async () => {
  try {
    // TODO: 调用获取学生名单API
    studentList.value = [
      {
        studentId: '2024001',
        name: '张三',
        className: '计算机1班',
        attendanceCount: 15,
        absenceCount: 1,
        attendanceRate: 94
      }
    ]
    studentTotal.value = 1
  } catch (error) {
    ElMessage.error('获取学生名单失败')
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'ONGOING': 'success',
    'ENDED': 'info',
    'NOT_STARTED': 'warning'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'ONGOING': '进行中',
    'ENDED': '已结束',
    'NOT_STARTED': '未开始'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchCourseList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    semester: '',
    status: ''
  })
  handleSearch()
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出课程数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 查看学生名单
const handleViewStudents = (row) => {
  showStudentsDialog.value = true
  fetchStudentList()
}

// 查看考勤记录
const handleViewAttendance = (row) => {
  // TODO: 跳转到考勤记录页面
}

// 编辑课程
const handleEdit = (row) => {
  Object.assign(courseForm, row)
  showEditDialog.value = true
}

// 提交编辑
const handleSubmitEdit = async () => {
  if (!courseFormRef.value) return
  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用更新课程API
        ElMessage.success('保存成功')
        showEditDialog.value = false
        fetchCourseList()
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 查看学生详情
const handleViewStudentDetail = (row) => {
  // TODO: 跳转到学生详情页面
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourseList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourseList()
}

// 学生名单分页大小改变
const handleStudentSizeChange = (val) => {
  studentPageSize.value = val
  fetchStudentList()
}

// 学生名单当前页改变
const handleStudentCurrentChange = (val) => {
  studentCurrentPage.value = val
  fetchStudentList()
}

onMounted(() => {
  fetchStatistics()
  fetchCourseList()
})
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

.statistics-cards {
  margin-bottom: 20px;
}

.statistics-card {
  .statistics-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .statistics-value {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
    text-align: center;
    margin-top: 10px;
  }
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

.mx-2 {
  margin: 0 8px;
}
</style>