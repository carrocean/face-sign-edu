<template>
  <div class="leave-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAddLeave">
              <el-icon>
                <Plus />
              </el-icon>
              请假申请
            </el-button>
          </div>
        </div>
      </template>


      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
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
        <el-table-column prop="approverId" label="审批人" align="center">
          <template #default="scope">
            {{ getTeacherName(scope.row.approverId) }}
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
        <el-descriptions-item label="请假时间">{{ parseTime(currentLeave.startTime) }} - {{ parseTime(currentLeave.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="请假原因">{{ currentLeave.reason }}</el-descriptions-item>
        <el-descriptions-item label="请假状态">
          <el-tag :type="getStatusType(currentLeave.status)">
            {{ getStatusLabel(currentLeave.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ getTeacherName(currentLeave.approverId) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 添加请假对话框 -->
    <el-dialog
        v-model="showAddDialog"
        title="添加通知"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-form :model="addLeaveForm" ref="addLeaveFormRef" label-width="80px">
        <el-form-item label="开始时间" prop="startTime" :rules="[{ required: true, message: '请选择开始时间', trigger: 'blur' }]">
          <el-date-picker v-model="addLeaveForm.startTime" type="datetime" placeholder="请选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime" :rules="[{ required: true, message: '请选择结束时间', trigger: 'blur' }]">
          <el-date-picker v-model="addLeaveForm.endTime" type="datetime" placeholder="请选择结束时间" />
        </el-form-item>
        <el-form-item label="请假原因" prop="reason" :rules="[{ required: true, message: '请输入请假原因', trigger: 'blur' }]">
          <el-input v-model="addLeaveForm.reason" type="textarea" placeholder="请输入请假原因" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="confirmAddLeave">确定</el-button>
          <el-button @click="cancelAddLeave">取消</el-button>
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
import {
  getAttendanceRecordsByStudent,
  updateLeaveRequest,
  deleteLeaveRequest,
  saveLeaveRequest
} from '@/api/leaveRequest.js'
import {getAllClasses} from '@/api/class.js'
import {getAllStudents} from '@/api/student.js'
import {Delete, Download, Plus, Upload} from "@element-plus/icons-vue";
import {parseTime} from "@/utils/Utils.js";
import {getAllTeachers} from "@/api/teacher.js";

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

const showAddDialog = ref(false)
const addLeaveForm = reactive({
  startTime: '',
  endTime: '',
  reason: ''
})
const addLeaveFormRef = ref(null)

const studentOptions = ref([])
const teacherOptions = ref([])


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

async function fetchLeaves() {
  loading.value = true
  try {
    const res = await getAttendanceRecordsByStudent(pageParams, searchForm)
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

// 获取学生名称
function getStudentName(studentId) {
  const student = studentOptions.value.find(item => item.id == studentId)
  return student ? student.name : ''
}

// 获取教师名称
function getTeacherName(teacherId) {
  const teacher = teacherOptions.value.find(item => item.id == teacherId)
  return teacher ? teacher.name : ''
}

// 处理分页大小变化
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchLeaves()
}

// 处理页码变化
function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchLeaves()
}

// 处理添加请假
function handleAddLeave() {
  showAddDialog.value = true
}

// 确认添加请假
async function confirmAddLeave() {
  await addLeaveFormRef.value.validate()
  if (new Date(addLeaveForm.startTime) > new Date(addLeaveForm.endTime)) {
    ElMessage.error('开始时间不能晚于结束时间')
    return
  }
  try {
    await saveLeaveRequest(addLeaveForm)
    ElMessage.success('添加成功')
    resetAddForm()
    showAddDialog.value = false
    await fetchLeaves()
  } catch (error) {
    console.error('添加失败:', error)
  }
}

// 取消添加请假
function cancelAddLeave() {
  resetAddForm()
  showAddDialog.value = false
}

// 重置表单
function resetAddForm() {
  Object.keys(addLeaveForm).forEach(key => {
    addLeaveForm[key] = ''
  })
}

onMounted(() => {
  fetchLeaves()
  fetchStudentOptions()
  fetchTeacherOptions()
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