<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="searchForm.teacherId" placeholder="请选择教师" filterable>
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 课程表格 -->
      <el-table :data="courseList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacherName" label="授课教师" />
        <el-table-column prop="schedule" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="attendanceRate" label="出勤率">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.attendanceRate"
              :status="getProgressStatus(scope.row.attendanceRate)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">查看详情</el-button>
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

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="课程详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ courseDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ courseDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ courseDetail.schedule }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ courseDetail.location }}</el-descriptions-item>
        <el-descriptions-item label="出勤率">
          <el-progress
            :percentage="courseDetail.attendanceRate"
            :status="getProgressStatus(courseDetail.attendanceRate)"
          />
        </el-descriptions-item>
        <el-descriptions-item label="课程状态">
          <el-tag :type="getStatusType(courseDetail.status)">
            {{ getStatusText(courseDetail.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <div class="mt-20">
        <h3>考勤记录</h3>
        <el-table :data="courseDetail.attendanceRecords" style="width: 100%">
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="time" label="时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'present' ? 'success' : 'danger'">
                {{ scope.row.status === 'present' ? '已签到' : '未签到' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  teacherId: ''
})

// 表格数据
const loading = ref(false)
const courseList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 教师列表
const teacherList = ref([])

// 对话框相关
const dialogVisible = ref(false)
const courseDetail = reactive({
  name: '',
  teacherName: '',
  schedule: '',
  location: '',
  attendanceRate: 0,
  status: '',
  attendanceRecords: []
})

// 获取进度条状态
const getProgressStatus = (rate) => {
  if (rate >= 90) return 'success'
  if (rate >= 80) return 'warning'
  return 'exception'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'active': 'success',
    'ended': 'info'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'active': '进行中',
    'ended': '已结束'
  }
  return texts[status] || status
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  fetchCourseList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.teacherId = ''
  handleSearch()
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

// 查看课程详情
const handleViewDetail = (row) => {
  // TODO: 调用后端API获取课程详情
  // 模拟数据
  Object.assign(courseDetail, {
    ...row,
    attendanceRecords: [
      {
        date: '2024-03-24',
        time: '08:00',
        status: 'present'
      },
      {
        date: '2024-03-17',
        time: '08:00',
        status: 'present'
      },
      {
        date: '2024-03-10',
        time: '08:00',
        status: 'absent'
      }
    ]
  })
  dialogVisible.value = true
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourseList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourseList()
}

// 初始化
fetchCourseList()
</script>

<style scoped>
.courses-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 