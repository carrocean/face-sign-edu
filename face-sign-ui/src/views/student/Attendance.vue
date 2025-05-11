<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤记录管理</span>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="考勤状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="出勤" value="PRESENT"/>
            <el-option label="迟到" value="LATE"/>
            <el-option label="缺勤" value="ABSENT"/>
            <el-option label="请假" value="LEAVE"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 考勤记录表格 -->
      <el-table
          :data="attendanceList"
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="courseName" label="课程名称" align="center">
          <template #default="scope">
            {{ scope.row.courseName }}
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="教师名称" align="center">
          <template #default="scope">
            {{ scope.row.teacherName }}
          </template>
        </el-table-column>
        <el-table-column label="上课时间" align="center">
          <template #default="scope">
            第{{ scope.row.theWeek }}周 星期{{ getWeekDayText(scope.row.weekDay) }} 第{{ scope.row.period }}节
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="考勤日期" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.attendanceDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="考勤状态" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
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

    <!-- 考勤详情对话框 -->
    <el-dialog
        v-model="showDetailDialog"
        title="考勤详情"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ currentAttendance.studentName }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ currentAttendance.courseName }}</el-descriptions-item>
        <el-descriptions-item label="教师名称">{{ currentAttendance.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">
          第{{ currentAttendance.theWeek }}周 星期{{ getWeekDayText(currentAttendance.weekDay) }} 第{{ currentAttendance.period }}节
        </el-descriptions-item>
        <el-descriptions-item label="考勤日期">{{ parseTime(currentAttendance.attendanceDate) }}</el-descriptions-item>
        <el-descriptions-item label="考勤状态">
          <el-tag :type="getStatusType(currentAttendance.status)">
            {{ getStatusText(currentAttendance.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Download, Delete, Calendar, Check, Timer, Close} from '@element-plus/icons-vue'
import {parseTime} from '@/utils/Utils'
import {getAllStudents} from '@/api/student.js'
import {getAllCourses} from '@/api/course.js'
import {getAttendanceRecordsByStudent, updateAttendanceRecord} from '@/api/attendanceRecord.js'
import {getAllCourseSchedules} from '@/api/courseSchedule.js'

// 搜索表单
const searchForm = reactive({
  studentId: '',
  courseId: '',
  status: ''
})

// 选项数据
const studentOptions = ref([])
const courseOptions = ref([])
const scheduleOptions = ref([])

// 考勤记录列表数据
const attendanceList = ref([])
const loading = ref(false)
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

// 详情对话框
const showDetailDialog = ref(false)
const currentAttendance = reactive({})

// 状态选择对话框
const showStatusDialog = ref(false)

// 统计数据
const statistics = reactive({
  totalCount: 0,
  attendanceRate: 0,
  lateRate: 0,
  absentRate: 0
})

// 计算统计数据
function calculateStatistics(records) {
  const total = records.length
  if (total === 0) {
    Object.assign(statistics, {
      totalCount: 0,
      attendanceRate: 0,
      lateRate: 0,
      absentRate: 0
    })
    return
  }

  const counts = {
    PRESENT: 0,
    LATE: 0,
    ABSENT: 0,
    LEAVE: 0
  }

  records.forEach(record => {
    counts[record.status] = (counts[record.status] || 0) + 1
  })

  Object.assign(statistics, {
    totalCount: total,
    attendanceRate: Math.round((counts.PRESENT / total) * 100),
    lateRate: Math.round((counts.LATE / total) * 100),
    absentRate: Math.round((counts.ABSENT / total) * 100)
  })
}

// 初始化数据
onMounted(async () => {
  await Promise.all([
    fetchStudentOptions(),
    fetchCourseOptions(),
    fetchScheduleOptions(),
    fetchAttendanceList()
  ])
})

// 获取学生选项
async function fetchStudentOptions() {
  try {
    const res = await getAllStudents()
    studentOptions.value = res.data
  } catch (error) {
    console.error('获取学生列表失败:', error)
  }
}

// 获取课程选项
async function fetchCourseOptions() {
  try {
    const res = await getAllCourses()
    courseOptions.value = res.data
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 获取课程安排选项
async function fetchScheduleOptions() {
  try {
    const res = await getAllCourseSchedules()
    scheduleOptions.value = res.data
  } catch (error) {
    console.error('获取课程安排列表失败:', error)
  }
}

// 获取考勤记录列表
async function fetchAttendanceList() {
  loading.value = true
  try {
    const res = await getAttendanceRecordsByStudent(pageParams,searchForm)
    attendanceList.value = res.data.records
    pageParams.total = res.data.total
    // 计算统计数据
    calculateStatistics(res.data.records)
  } catch (error) {
    console.error('获取考勤记录列表失败:', error)
    ElMessage.error('获取考勤记录列表失败')
  } finally {
    loading.value = false
  }
}

// 获取学生名称
function getStudentName(studentId) {
  const student = studentOptions.value.find(item => item.id === studentId)
  return student ? student.name : ''
}

// 获取课程名称
function getCourseName(courseId) {
  const course = courseOptions.value.find(item => item.id === courseId)
  return course ? course.courseName : ''
}

// 获取课程安排信息
function getWeekDayText(weekDay) {
  const weekDays = ['一', '二', '三', '四', '五', '六', '日'];
  return weekDays[weekDay - 1]; // weekDay 从 1 开始，数组索引从 0 开始
}

// 获取状态类型
function getStatusType(status) {
  const typeMap = {
    PRESENT: 'success',
    LATE: 'warning',
    ABSENT: 'danger',
    LEAVE: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
function getStatusText(status) {
  const textMap = {
    PRESENT: '出勤',
    LATE: '迟到',
    ABSENT: '缺勤',
    LEAVE: '请假'
  }
  return textMap[status] || status
}

// 处理搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchAttendanceList()
}

// 重置搜索
function resetSearch() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pageParams.currentPage = 1
  pageParams.fuzzySearch = true
  fetchAttendanceList()
}

// 处理分页大小变化
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchAttendanceList()
}

// 处理页码变化
function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchAttendanceList()
}

// 处理查看详情
function handleViewDetail(row) {
  Object.assign(currentAttendance, row)
  showDetailDialog.value = true
}


</script>

<style scoped>
.attendance-container {
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

.statistics-cards {
  margin-bottom: 20px;
}

.statistics-card {
  text-align: center;
}

.statistics-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-card .card-content {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style>