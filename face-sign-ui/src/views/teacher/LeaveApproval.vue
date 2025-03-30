<template>
  <div class="leave-approval-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假审批</span>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="待审批" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="请假类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型">
            <el-option label="事假" value="PERSONAL" />
            <el-option label="病假" value="SICK" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
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
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 请假申请列表 -->
      <el-table
        v-loading="loading"
        :data="leaveList"
        style="width: 100%"
        border
      >
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="type" label="请假类型" width="100">
          <template #default="scope">
            {{ getLeaveTypeName(scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="scope">
            {{ scope.row.duration }}天
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="success"
              link
              @click="handleApprove(scope.row)"
            >
              批准
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="danger"
              link
              @click="handleReject(scope.row)"
            >
              拒绝
            </el-button>
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
              详情
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

    <!-- 审批对话框 -->
    <el-dialog
      v-model="showApprovalDialog"
      :title="approvalType === 'approve' ? '批准请假' : '拒绝请假'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="approvalFormRef"
        :model="approvalForm"
        :rules="approvalRules"
        label-width="100px"
      >
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="approvalType === 'approve' ? '请输入批准意见（选填）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showApprovalDialog = false">取消</el-button>
          <el-button
            :type="approvalType === 'approve' ? 'success' : 'danger'"
            @click="handleSubmitApproval"
            :loading="submitting"
          >
            {{ approvalType === 'approve' ? '批准' : '拒绝' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 请假详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="请假详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentLeave.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentLeave.studentId }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ currentLeave.className }}</el-descriptions-item>
        <el-descriptions-item label="请假类型">{{ getLeaveTypeName(currentLeave.type) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentLeave.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentLeave.endTime }}</el-descriptions-item>
        <el-descriptions-item label="请假时长">{{ currentLeave.duration }}天</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(currentLeave.status)">
            {{ getStatusText(currentLeave.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">
          {{ currentLeave.reason }}
        </el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <el-button type="primary" link @click="handleDownloadAttachment">
            <el-icon><Download /></el-icon>
            下载附件
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">
          {{ currentLeave.approvalComment || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="审批时间">
          {{ currentLeave.approvalTime || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="审批人">
          {{ currentLeave.approver || '暂无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Search,
  Refresh
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  status: '',
  type: '',
  dateRange: []
})

// 请假列表数据
const loading = ref(false)
const leaveList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 审批对话框
const showApprovalDialog = ref(false)
const approvalType = ref('approve')
const submitting = ref(false)
const approvalFormRef = ref(null)
const approvalForm = reactive({
  comment: ''
})

// 表单验证规则
const approvalRules = {
  comment: [
    { required: true, message: '请输入审批意见', trigger: 'blur' }
  ]
}

// 请假详情对话框
const showDetailDialog = ref(false)
const currentLeave = ref({})

// 获取请假类型名称
const getLeaveTypeName = (type) => {
  const types = {
    'PERSONAL': '事假',
    'SICK': '病假',
    'OTHER': '其他'
  }
  return types[type] || type
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审批',
    'APPROVED': '已批准',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}

// 获取请假列表
async function fetchLeaveList() {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const res = await getAllLeaveRequests(params, searchForm)
    if (res.code === 200) {
      leaveList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取请假列表失败')
    }
  } catch (error) {
    console.error('获取请假列表失败:', error)
    ElMessage.error('获取请假列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchLeaveList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    status: '',
    type: '',
    dateRange: []
  })
  handleSearch()
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出请假记录API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 批准请假
const handleApprove = (row) => {
  approvalType.value = 'approve'
  currentLeave.value = row
  showApprovalDialog.value = true
}

// 拒绝请假
const handleReject = (row) => {
  approvalType.value = 'reject'
  currentLeave.value = row
  showApprovalDialog.value = true
}

// 提交审批
const handleSubmitApproval = async () => {
  if (!approvalFormRef.value) return
  await approvalFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用审批API
        ElMessage.success(approvalType.value === 'approve' ? '批准成功' : '拒绝成功')
        showApprovalDialog.value = false
        fetchLeaveList()
      } catch (error) {
        ElMessage.error(approvalType.value === 'approve' ? '批准失败' : '拒绝失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 查看详情
const handleViewDetail = (row) => {
  currentLeave.value = row
  showDetailDialog.value = true
}

// 下载附件
const handleDownloadAttachment = async () => {
  try {
    // TODO: 调用下载附件API
    ElMessage.success('下载成功')
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchLeaveList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchLeaveList()
}

onMounted(() => {
  fetchLeaveList()
})
</script>

<style scoped>
.leave-approval-container {
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
</style> 