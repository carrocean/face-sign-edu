<template>
  <div class="logs-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出日志
            </el-button>
            <el-button type="danger" @click="handleClear">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择操作类型">
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
            <el-option label="添加" value="ADD" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
            <el-option label="导出" value="EXPORT" />
            <el-option label="导入" value="IMPORT" />
            <el-option label="系统设置" value="SETTINGS" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作模块">
          <el-select v-model="searchForm.module" placeholder="请选择操作模块">
            <el-option label="用户管理" value="USER" />
            <el-option label="学生管理" value="STUDENT" />
            <el-option label="教师管理" value="TEACHER" />
            <el-option label="班级管理" value="CLASS" />
            <el-option label="课程管理" value="COURSE" />
            <el-option label="考勤管理" value="ATTENDANCE" />
            <el-option label="系统管理" value="SYSTEM" />
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

      <!-- 日志列表 -->
      <el-table
        v-loading="loading"
        :data="logList"
        style="width: 100%"
        border
      >
        <el-table-column prop="timestamp" label="操作时间" width="180" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="module" label="操作模块" width="120">
          <template #default="scope">
            {{ getModuleName(scope.row.module) }}
          </template>
        </el-table-column>
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="scope">
            {{ getOperationTypeName(scope.row.operationType) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">
              {{ scope.row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">
              详情
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              删除
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

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="日志详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作时间">{{ currentLog.timestamp }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ getModuleName(currentLog.module) }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ getOperationTypeName(currentLog.operationType) }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 'SUCCESS' ? 'success' : 'danger'">
            {{ currentLog.status === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">{{ currentLog.description }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre>{{ currentLog.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <pre>{{ currentLog.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.status === 'FAILED'">
          <pre class="error-message">{{ currentLog.errorMessage }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Delete
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  dateRange: [],
  operator: '',
  operationType: '',
  module: ''
})

// 日志列表数据
const loading = ref(false)
const logList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 日志详情对话框
const showDetailDialog = ref(false)
const currentLog = ref({})

// 获取模块名称
const getModuleName = (module) => {
  const moduleMap = {
    USER: '用户管理',
    STUDENT: '学生管理',
    TEACHER: '教师管理',
    CLASS: '班级管理',
    COURSE: '课程管理',
    ATTENDANCE: '考勤管理',
    SYSTEM: '系统管理'
  }
  return moduleMap[module] || module
}

// 获取操作类型名称
const getOperationTypeName = (type) => {
  const typeMap = {
    LOGIN: '登录',
    LOGOUT: '登出',
    ADD: '添加',
    UPDATE: '修改',
    DELETE: '删除',
    EXPORT: '导出',
    IMPORT: '导入',
    SETTINGS: '系统设置'
  }
  return typeMap[type] || type
}

// 获取日志列表
const fetchLogs = async () => {
  loading.value = true
  try {
    // TODO: 调用获取日志列表API
    logList.value = [
      {
        id: 1,
        timestamp: '2024-03-28 10:00:00',
        operator: 'admin',
        module: 'USER',
        operationType: 'LOGIN',
        description: '用户登录系统',
        ip: '192.168.1.100',
        status: 'SUCCESS',
        requestParams: '{"username": "admin"}',
        responseResult: '{"code": 200, "message": "登录成功"}'
      }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchLogs()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    dateRange: [],
    operator: '',
    operationType: '',
    module: ''
  })
  handleSearch()
}

// 导出日志
const handleExport = async () => {
  try {
    // TODO: 调用导出日志API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 清空日志
const handleClear = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有日志吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用清空日志API
    ElMessage.success('清空成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 查看详情
const handleDetail = (row) => {
  currentLog.value = row
  showDetailDialog.value = true
}

// 删除日志
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该日志吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用删除日志API
    ElMessage.success('删除成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchLogs()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.logs-container {
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

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.error-message {
  color: #f56c6c;
}
</style> 