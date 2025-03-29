<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤记录管理</span>
          <div class="header-buttons">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form" label-width="80px">
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
              :key="item.classId"
              :label="item.name"
              :value="item.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable>
            <el-option
              v-for="item in courseOptions"
              :key="item.courseId"
              :label="item.name"
              :value="item.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="出勤" value="1" />
            <el-option label="迟到" value="2" />
            <el-option label="早退" value="3" />
            <el-option label="缺勤" value="4" />
            <el-option label="请假" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="statistics-card">
            <template #header>
              <div class="card-header">
                <span>总考勤次数</span>
                <el-icon><Calendar /></el-icon>
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
                <el-icon><Check /></el-icon>
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
                <el-icon><Timer /></el-icon>
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
                <el-icon><Close /></el-icon>
              </div>
            </template>
            <div class="card-content">
              <span class="number">{{ statistics.absentRate }}%</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 考勤记录列表 -->
      <el-table :data="attendanceList" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="courseName" label="课程" width="200" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="time" label="时间" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="打卡地点" width="200" />
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination background
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

    <!-- 统计图表对话框 -->
    <el-dialog
      v-model="showChartDialog"
      title="考勤统计图表"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="chart-container">
        <el-tabs v-model="activeChart">
          <el-tab-pane label="出勤率趋势" name="attendance">
            <div ref="attendanceChartRef" class="chart"></div>
          </el-tab-pane>
          <el-tab-pane label="考勤状态分布" name="status">
            <div ref="statusChartRef" class="chart"></div>
          </el-tab-pane>
          <el-tab-pane label="班级出勤对比" name="class">
            <div ref="classChartRef" class="chart"></div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, nextTick, watch} from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Calendar,
  Check,
  Timer,
  Close
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 搜索表单
const searchForm = reactive({
  studentId: '',
  name: '',
  classId: '',
  courseId: '',
  status: '',
  dateRange: []
})

// 选项数据
const classOptions = ref([])
const courseOptions = ref([])

// 统计数据
const statistics = reactive({
  totalCount: 0,
  attendanceRate: 0,
  lateRate: 0,
  absentRate: 0
})

// 考勤记录列表
const attendanceList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 图表相关
const showChartDialog = ref(false)
const activeChart = ref('attendance')
const attendanceChartRef = ref(null)
const statusChartRef = ref(null)
const classChartRef = ref(null)
let attendanceChart = null
let statusChart = null
let classChart = null

// 获取班级选项
const fetchClassOptions = async () => {
  try {
    // TODO: 调用获取班级列表API
    classOptions.value = [
      { classId: '2024001', name: '计算机科学1班' },
      { classId: '2024002', name: '计算机科学2班' }
    ]
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 获取课程选项
const fetchCourseOptions = async () => {
  try {
    // TODO: 调用获取课程列表API
    courseOptions.value = [
      { courseId: 'CS001', name: '计算机导论' },
      { courseId: 'CS002', name: '程序设计基础' }
    ]
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // TODO: 调用获取统计数据API
    Object.assign(statistics, {
      totalCount: 1000,
      attendanceRate: 95,
      lateRate: 3,
      absentRate: 2
    })
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取考勤记录列表
const fetchAttendanceList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取考勤记录列表API
    attendanceList.value = [
      {
        studentId: '20240001',
        name: '张三',
        className: '计算机科学1班',
        courseName: '计算机导论',
        date: '2024-03-28',
        time: '08:30:00',
        status: '1',
        location: '教学楼A101',
        remark: ''
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取考勤记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAttendanceList()
  fetchStatistics()
}

// 重置搜索
const resetSearch = () => {
  searchForm.studentId = ''
  searchForm.name = ''
  searchForm.classId = ''
  searchForm.courseId = ''
  searchForm.status = ''
  searchForm.dateRange = []
  handleSearch()
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    '1': 'success', // 出勤
    '2': 'warning', // 迟到
    '3': 'warning', // 早退
    '4': 'danger',  // 缺勤
    '5': 'info'     // 请假
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    '1': '出勤',
    '2': '迟到',
    '3': '早退',
    '4': '缺勤',
    '5': '请假'
  }
  return texts[status] || '未知'
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出考勤数据API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 出勤率趋势图
  attendanceChart = echarts.init(attendanceChartRef.value)
  attendanceChart.setOption({
    title: {
      text: '近30天出勤率趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['3-1', '3-2', '3-3', '3-4', '3-5']
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [{
      data: [95, 93, 96, 94, 95],
      type: 'line',
      smooth: true
    }]
  })

  // 考勤状态分布图
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    title: {
      text: '考勤状态分布'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 950, name: '出勤' },
        { value: 30, name: '迟到' },
        { value: 20, name: '缺勤' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  // 班级出勤对比图
  classChart = echarts.init(classChartRef.value)
  classChart.setOption({
    title: {
      text: '班级出勤率对比'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ['计算机科学1班', '计算机科学2班']
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [{
      data: [95, 93],
      type: 'bar'
    }]
  })
}

// 监听图表标签页切换
watch(activeChart, () => {
  nextTick(() => {
    switch (activeChart.value) {
      case 'attendance':
        attendanceChart?.resize()
        break
      case 'status':
        statusChart?.resize()
        break
      case 'class':
        classChart?.resize()
        break
    }
  })
})

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchAttendanceList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchAttendanceList()
}

onMounted(() => {
  fetchClassOptions()
  fetchCourseOptions()
  fetchStatistics()
  fetchAttendanceList()
})
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.chart-container {
  height: 400px;
}

.chart {
  height: 100%;
  width: 100%;
}
</style>