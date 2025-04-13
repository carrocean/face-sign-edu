<template>
  <div class="logs-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
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
        <el-form-item label="操作人">
          <el-input v-model="searchForm.userId" placeholder="请输入操作人ID" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-input v-model="searchForm.operation" placeholder="请输入操作类型" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="IP地址">
          <el-input v-model="searchForm.ipAddress" placeholder="请输入IP地址" clearable style="width: 200px"/>
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

      <!-- 日志表格 -->
      <el-table
          :data="logList"
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="ID" width="80" align="center"/>
        <el-table-column prop="userId" label="操作人ID" align="center"/>
        <el-table-column prop="operation" label="操作类型" align="center"/>
        <el-table-column prop="operationTime" label="操作时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.operationTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" align="center"/>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            v-model:current-page="pageParams.currentPage"
            v-model:page-size="pageParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pageParams.total"
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
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人ID">{{ currentLog.userId }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ parseTime(currentLog.operationTime) }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">
          <div class="operation-content">{{ currentLog.operation }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Download} from '@element-plus/icons-vue'
import {parseTime} from '@/utils/Utils'
import {getAllPageSystemLogs, exportLogs} from '@/api/systemLog.js'

// 搜索表单
const searchForm = reactive({
  userId: '',
  operation: '',
  ipAddress: '',
})

// 日志列表数据
const logList = ref([])
const loading = ref(false)
const pageParams = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  fuzzySearch: true  // 默认启用模糊查询
})

// 详情对话框
const showDetailDialog = ref(false)
const currentLog = reactive({
  userId: '',
  operation: '',
  operationTime: '',
  ipAddress: ''
})

// 初始化数据
onMounted(async () => {
  await fetchLogList()
})

// 获取日志列表
async function fetchLogList() {
  loading.value = true
  try {
    const res = await getAllPageSystemLogs(pageParams, searchForm)
    logList.value = res.data.records
    pageParams.total = res.data.total
  } catch (error) {
    console.error('获取日志列表失败:', error)
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
function handleSearch() {
  pageParams.currentPage = 1
  fetchLogList()
}

// 重置搜索
function resetSearch() {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pageParams.currentPage = 1
  fetchLogList()
}

// 处理分页大小变化
function handleSizeChange(val) {
  pageParams.pageSize = val
  fetchLogList()
}

// 处理页码变化
function handleCurrentChange(val) {
  pageParams.currentPage = val
  fetchLogList()
}

// 处理查看详情
function handleViewDetail(row) {
  Object.assign(currentLog, row)
  showDetailDialog.value = true
}

// 处理导出 TODO
async function handleExport() {
  try {
    const params = {
      ...searchForm,
      startTime: searchForm.dateRange?.[0],
      endTime: searchForm.dateRange?.[1]
    }
    await exportLogs(params)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}
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

.operation-content {
  white-space: pre-wrap;
  word-break: break-all;
}
</style> 