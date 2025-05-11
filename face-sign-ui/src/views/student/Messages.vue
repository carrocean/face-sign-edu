<template>
  <div class="notification-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
        </div>
      </template>


      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题名称" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.isRead" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="未读" value="0" />
            <el-option label="已读" value="1" />
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


      <!-- 通知表格 -->
      <el-table
          :data="notificationList"
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="title" label="标题" align="center">
          <template #default="scope">
            {{ scope.row.title }}
          </template>
        </el-table-column>
        <el-table-column prop="senderId" label="发送者" align="center">
          <template #default="scope">
            {{ getName(scope.row.senderId) }}
          </template>
        </el-table-column>
        <el-table-column prop="receiverId" label="接收者" align="center">
          <template #default="scope">
            {{ getName(scope.row.receiverId) }}
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" align="center">
          <template #default="scope">
            <el-tag :type="getIsReadType(scope.row.isRead)">
              {{ getIsReadLabel(scope.row.isRead) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
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

    <!-- 通知详情对话框 -->
    <el-dialog
        v-model="showDetailDialog"
        title="通知详情"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="通知标题">{{ currentNotification.title }}</el-descriptions-item>
        <el-descriptions-item label="通知内容">{{ currentNotification.content }}</el-descriptions-item>
        <el-descriptions-item label="发送者">{{ getName(currentNotification.senderId) }}</el-descriptions-item>
        <el-descriptions-item label="接收者">{{ getName(currentNotification.receiverId) }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ parseTime(currentNotification.sendTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>


  </div>
</template>

<script setup>
import {ref, onMounted, reactive} from 'vue'
import {
  ElMessage,
  ElTag,
  ElForm,
  ElFormItem,
  ElSelect,
  ElOption,
  ElDatePicker,
  ElButton,
  ElMessageBox
} from 'element-plus'
import { getAttendanceRecordsByStudent, updateNotification, deleteNotification, saveNotification } from '@/api/notification.js'
import {getAllAdministrators} from '@/api/administrator.js'
import {getAllStudents} from '@/api/student.js'
import {getAllTeachers} from '@/api/teacher.js'
import {getAllUsers} from '@/api/user.js'
import {Delete, Download, Plus, Upload} from "@element-plus/icons-vue";
import {parseTime} from "@/utils/Utils.js";

const notificationList = ref([])
const searchForm = reactive({
})
const loading = ref(false)
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

const userOptions = ref([])
const adminOptions = ref([])
const studentOptions = ref([])
const teacherOptions = ref([])

// 获取状态标签类型
const getIsReadType = (isRead) => {
  const types = {
    '0': 'warning',
    '1': 'success',
  }
  return types[isRead] || 'info'
}

// 获取状态显示文本
const getIsReadLabel = (isRead) => {
  const labels = {
    '0': '未读',
    '1': '已读'
  }
  return labels[isRead] || read
}

// 详情对话框
const showDetailDialog = ref(false)
const currentNotification = reactive({})
// 添加通知对话框
const showAddDialog = ref(false)
const addNotificationForm = reactive({
  senderId: '',
  receiverId: '',
  title: '',
  content: ''
})

// 获取用户选项
async function fetchUserOptions() {
  try {
    const res = await getAllUsers()
    userOptions.value = res.data
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 获取管理员选项
async function fetchAdminOptions() {
  try {
    const res = await getAllAdministrators()
    adminOptions.value = res.data
  } catch (error) {
    console.error('获取管理员列表失败:', error)
  }
}

// 获取学生选项
async function fetchStudentOptions() {
  try {
    const res = await getAllStudents()
    studentOptions.value = res.data
  } catch (error) {
    console.error('获取学生列表失败:', error)
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

async function fetchNotifications() {
  loading.value = true
  try {
    const res = await getAttendanceRecordsByStudent(pageParams, searchForm)
    notificationList.value = res.data.records
    pageParams.total = res.data.total
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理查看详情
function handleViewDetail(row) {
  const updateRead = reactive({
    id: row.id,
    isRead: "1"
  })
  updateNotification(updateRead)
  fetchNotifications()

  Object.assign(currentNotification, row)
  showDetailDialog.value = true
}

// 处理搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchNotifications()
}

// 重置搜索
function resetSearch() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pageParams.currentPage = 1
  pageParams.fuzzySearch = true
  fetchNotifications()
}

// 获取学生名称
function getName(userId) {
  const user = userOptions.value.find(item => item.id == userId)
  if (user.role === 'admin') {
    const admin = adminOptions.value.find(item => item.userId == userId)
    return admin ? admin.name : ''
  } else if(user.role === 'teacher') {
    const teacher = teacherOptions.value.find(item => item.userId == userId)
    return teacher ? teacher.name : ''
  } else if(user.role === 'student') {
    const student = studentOptions.value.find(item => item.userId == userId)
    return student ? student.name : ''
  }
  return ''
}


// 处理分页大小变化
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchNotifications()
}

// 处理页码变化
function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchNotifications()
}

// 处理导出 TODO
async function handleExport() {
  try {
    const params = {
      ...searchForm,
      fuzzySearch: pageParams.fuzzySearch
    }
    // await exportCourses(params)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
  }
}

onMounted(() => {
  fetchNotifications()
  fetchStudentOptions()
  fetchTeacherOptions()
  fetchAdminOptions()
  fetchUserOptions()
})
</script>

<style scoped>
.notification-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>