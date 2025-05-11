<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤记录管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleExport">
              <el-icon>
                <Download/>
              </el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="card-header">
                <span>总考勤次数</span>
                <el-icon><Calendar/></el-icon>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ statistics.totalCount }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="card-header">
                <span>出勤率</span>
                <el-icon><Check/></el-icon>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ statistics.attendanceRate }}%</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="card-header">
                <span>迟到率</span>
                <el-icon><Timer/></el-icon>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ statistics.lateRate }}%</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="card-header">
                <span>缺勤率</span>
                <el-icon><Close/></el-icon>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ statistics.absentRate }}%</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学生">
          <el-select v-model="searchForm.studentId" placeholder="请选择学生" clearable style="width: 200px">
            <el-option
                v-for="item in studentOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable style="width: 200px">
            <el-option
                v-for="item in courseOptions"
                :key="item.id"
                :label="item.courseName"
                :value="item.id"
            />
          </el-select>
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

      <!-- 考勤记录表格 -->
      <el-table
          :data="attendanceList"
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="studentName" label="学生" align="center"/>
        <el-table-column prop="courseName" label="课程" align="center"/>
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
            <el-button type="primary" link @click="handleAdjustStatus(scope.row)">调整状态</el-button>
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
        <el-descriptions-item label="课程">{{ currentAttendance.courseName }}</el-descriptions-item>
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

    <!-- 状态选择对话框 -->
    <el-dialog
        v-model="showStatusDialog"
        title="调整考勤状态"
        width="300px"
        :close-on-click-modal="false"
    >
      <el-form :model="currentAttendance" ref="statusForm" label-width="80px">
        <el-form-item label="考勤状态">
          <el-select v-model="currentAttendance.status" placeholder="请选择状态">
            <el-option label="出勤" value="PRESENT"/>
            <el-option label="迟到" value="LATE"/>
            <el-option label="缺勤" value="ABSENT"/>
            <el-option label="请假" value="LEAVE"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="confirmAdjustStatus">确定</el-button>
          <el-button @click="cancelAdjustStatus">取消</el-button>
        </el-form-item>
      </el-form>
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
import {
  exportAdminAttendanceRecords,
  exportTeacherAttendanceRecords,
  getAllPageAttendanceRecords,
  updateAttendanceRecord
} from '@/api/attendanceRecord.js'
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
    const res = await getAllPageAttendanceRecords(pageParams,searchForm)
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
function getScheduleInfo(scheduleId) {
  const schedule = scheduleOptions.value.find(item => item.id === scheduleId)
  if (!schedule) return ''
  
  const weekDayMap = {
    1: '周一',
    2: '周二',
    3: '周三',
    4: '周四',
    5: '周五',
    6: '周六',
    7: '周日'
  }
  
  return `${weekDayMap[schedule.weekDay]} ${schedule.classroom}`
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

// 处理导出
async function handleExport() {
  try {
    const params = {
      ...searchForm
    }
    const res = await exportAdminAttendanceRecords(params)
    if (res) {
      let blob = new Blob([res], { type: 'application/vnd.ms-excel;charset=utf-8' })
      let downloadElement = document.createElement('a');
      let href = window.URL.createObjectURL(blob); //创建下载的链接
      downloadElement.href = href;
      downloadElement.download = '考勤记录.xlsx'; //下载后文件名
      document.body.appendChild(downloadElement);
      downloadElement.click(); //点击下载
      document.body.removeChild(downloadElement); //下载完成移除元素
      window.URL.revokeObjectURL(href); //释放掉blob对象
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 处理调整状态
function handleAdjustStatus(row) {
  Object.assign(currentAttendance, row)
  showStatusDialog.value = true
}

// 确认调整状态
async function confirmAdjustStatus() {
  try {
    await updateAttendanceRecord(currentAttendance)
    ElMessage.success('考勤状态调整成功')
    showStatusDialog.value = false
    fetchAttendanceList()
  } catch (error) {
    console.error('调整考勤状态失败:', error)
    ElMessage.error('调整考勤状态失败')
  }
}

// 获取课程安排信息
function getWeekDayText(weekDay) {
  const weekDays = ['一', '二', '三', '四', '五', '六', '日'];
  return weekDays[weekDay]; // weekDay 从 1 开始，数组索引从 0 开始
}

// 取消调整状态
function cancelAdjustStatus() {
  showStatusDialog.value = false
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