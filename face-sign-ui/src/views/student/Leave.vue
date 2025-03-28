<template>
  <div class="leave-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假记录</span>
          <div class="header-buttons">
            <el-button type="primary" @click="showExportDialog = true">
              <el-icon><Download /></el-icon>
              导出请假记录
            </el-button>
            <el-button type="success" @click="showApplyDialog = true">
              <el-icon><Plus /></el-icon>
              申请请假
            </el-button>
          </div>
        </div>
      </template>

      <!-- 请假记录表格 -->
      <el-table :data="leaveRecords" style="width: 100%">
        <el-table-column prop="startDate" label="开始日期" width="180" />
        <el-table-column prop="endDate" label="结束日期" width="180" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'pending'"
              type="danger" 
              link 
              @click="handleCancel(scope.row)"
            >
              取消
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

    <!-- 导出对话框 -->
    <ExportDialog
      v-model:visible="showExportDialog"
      type="leave"
      :export-function="exportLeave"
    />

    <!-- 请假申请对话框 -->
    <el-dialog
      v-model="showApplyDialog"
      title="申请请假"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="leaveForm" label-width="100px">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="leaveForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="leaveForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="请假原因">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入请假原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showApplyDialog = false">取消</el-button>
          <el-button type="primary" @click="handleApply" :loading="applying">
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Download, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { exportLeave } from '@/api/export'
import ExportDialog from '@/components/common/ExportDialog.vue'

// 请假记录数据
const leaveRecords = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showExportDialog = ref(false)
const showApplyDialog = ref(false)
const applying = ref(false)

// 请假申请表单
const leaveForm = ref({
  startDate: '',
  endDate: '',
  reason: ''
})

// 获取请假记录
const fetchLeaveRecords = async () => {
  // TODO: 实现获取请假记录的API调用
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待审批',
    approved: '已批准',
    rejected: '已拒绝'
  }
  return texts[status] || '未知'
}

// 处理请假申请
const handleApply = async () => {
  if (!leaveForm.value.startDate || !leaveForm.value.endDate) {
    ElMessage.warning('请选择请假时间范围')
    return
  }

  if (!leaveForm.value.reason) {
    ElMessage.warning('请输入请假原因')
    return
  }

  applying.value = true
  try {
    // TODO: 实现请假申请的API调用
    ElMessage.success('请假申请提交成功')
    showApplyDialog.value = false
    fetchLeaveRecords()
  } catch (error) {
    ElMessage.error('请假申请提交失败')
  } finally {
    applying.value = false
  }
}

// 处理取消请假
const handleCancel = async (row) => {
  try {
    // TODO: 实现取消请假的API调用
    ElMessage.success('请假申请已取消')
    fetchLeaveRecords()
  } catch (error) {
    ElMessage.error('取消请假失败')
  }
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchLeaveRecords()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchLeaveRecords()
}

onMounted(() => {
  fetchLeaveRecords()
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

.header-buttons {
  display: flex;
  gap: 10px;
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