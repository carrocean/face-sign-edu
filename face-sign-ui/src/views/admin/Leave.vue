<template>
  <div class="leave-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假管理</span>
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
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="待审批" value="pending" />
            <el-option label="已批准" value="approved" />
            <el-option label="已拒绝" value="rejected" />
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


      <!-- 请假表格 -->
      <el-table
          :data="leaveList"
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="studentId" label="学生" align="center">
          <template #default="scope">
            {{ getStudentName(scope.row.studentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="classId" label="班级" align="center">
          <template #default="scope">
            {{ getClassName(scope.row.studentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addTime" label="创建时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.addTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
            <el-button type="primary" link @click="handleAdjustStatus(scope.row)">调整状态</el-button>
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

    <!-- 请假详情对话框 -->
    <el-dialog
        v-model="showDetailDialog"
        title="请假详情"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ getStudentName(currentLeave.studentId) }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ getClassName(currentLeave.studentId) }}</el-descriptions-item>
        <el-descriptions-item label="请假时间">{{ parseTime(currentLeave.startTime) }} - {{ parseTime(currentLeave.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="请假状态">
          <el-tag :type="getStatusType(currentLeave.status)">
            {{ getStatusLabel(currentLeave.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请假原因">{{ currentLeave.reason }}</el-descriptions-item>
        <el-descriptions-item label="添加时间">{{ parseTime(currentLeave.addTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(currentLeave.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 状态选择对话框 -->
    <el-dialog
        v-model="showStatusDialog"
        title="调整请假状态"
        width="300px"
        :close-on-click-modal="false"
    >
      <el-form :model="currentLeave" ref="statusForm" label-width="80px">
        <el-form-item label="请假状态">
          <el-select v-model="currentLeave.status" placeholder="请选择状态">
            <el-option label="待审批" value="pending"/>
            <el-option label="批准" value="approved"/>
            <el-option label="拒绝" value="rejected"/>
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
import { getAllPageLeaveRequests, updateLeaveRequest, deleteLeaveRequest } from '@/api/leaveRequest.js'
import {getAllClasses} from '@/api/class.js'
import {getAllStudents} from '@/api/student.js'
import {Delete, Download, Plus, Upload} from "@element-plus/icons-vue";
import {parseTime} from "@/utils/Utils.js";

const leaveList = ref([])
const searchForm = reactive({
  status: '',
})
const loading = ref(false)
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

const classOptions = ref([])
const studentOptions = ref([])

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusLabel = (status) => {
  const labels = {
    'pending': '待审批',
    'approved': '已批准',
    'rejected': '已拒绝'
  }
  return labels[status] || status
}

// 详情对话框
const showDetailDialog = ref(false)
const currentLeave = reactive({})

// 状态选择对话框
const showStatusDialog = ref(false)

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
async function fetchStudentOptions() {
  try {
    const res = await getAllStudents()
    studentOptions.value = res.data
  } catch (error) {
    console.error('获取学生列表失败:', error)
  }
}

async function fetchLeaves() {
  loading.value = true
  try {
    const res = await getAllPageLeaveRequests(pageParams, searchForm)
    leaveList.value = res.data.records
    pageParams.total = res.data.total
  } catch (error) {
    console.error('获取请假列表失败:', error)
    ElMessage.error('获取请假列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理查看详情
function handleViewDetail(row) {
  Object.assign(currentLeave, row)
  showDetailDialog.value = true
}

// 处理调整状态
function handleAdjustStatus(row) {
  Object.assign(currentLeave, row)
  showStatusDialog.value = true
}

// 确认调整状态
async function confirmAdjustStatus() {
  try {
    await updateLeaveRequest(currentLeave)
    ElMessage.success('请假状态调整成功')
    showStatusDialog.value = false
    fetchLeaves()
  } catch (error) {
    console.error('调整请假状态失败:', error)
    ElMessage.error('调整请假状态失败')
  }
}

// 取消调整状态
function cancelAdjustStatus() {
  showStatusDialog.value = false
}

// 处理删除
function handleDelete(row) {
  ElMessageBox.confirm(
      `确定要删除该请假申请吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      await deleteLeaveRequest(row.id)
      ElMessage.success('删除成功')
      await fetchLeaves()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

const handleApprove = async (row) => {
  try {
    const data = {
      'status': 'approved',
      'userId': row.userId
    }

    const res = await updateLeaveRequest(data)
    if (res.code === 200) {
      ElMessage.success('已批准')
      await fetchLeaves()
    } else {
      ElMessage.error(res.message || '批准失败')
    }
  } catch (error) {
    console.error('批准失败:', error)
    ElMessage.error('批准失败')
  }
}

const handleReject = async (row) => {
  try {
    const data = {
      'status': 'rejected',
      'userId': row.userId
    }

    const res = await updateLeaveRequest(data)
    if (res.code === 200) {
      ElMessage.success('已拒绝')
      await fetchLeaves()
    } else {
      ElMessage.error(res.message || '拒绝失败')
    }
  } catch (error) {
    console.error('拒绝失败:', error)
    ElMessage.error('拒绝失败')
  }
}

// 处理搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchLeaves()
}

// 重置搜索
function resetSearch() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pageParams.currentPage = 1
  pageParams.fuzzySearch = true
  fetchLeaves()
}

// 获取教师名称
function getStudentName(studentId) {
  const student = studentOptions.value.find(item => item.id === studentId)
  return student ? student.name : ''
}

// 获取班级名称
function getClassName(studentId) {
  const student = studentOptions.value.find(item => item.id === studentId);
  if (!student) return '';
  const classItem = classOptions.value.find(item => item.id === student.classId);
  return classItem ? classItem.className : '';
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
  fetchLeaves()
  fetchStudentOptions()
  fetchClassOptions()
})
</script>

<style scoped>
.leave-container {
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