<template>
  <div class="notifications-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <el-button type="primary" @click="handleSendNotification">
            <el-icon><Message /></el-icon>
            发送通知
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="通知类型">
          <el-select v-model="searchForm.type" placeholder="请选择通知类型">
            <el-option label="考勤提醒" value="ATTENDANCE" />
            <el-option label="课程变更" value="COURSE" />
            <el-option label="请假审批" value="LEAVE" />
            <el-option label="系统通知" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="已发送" value="SENT" />
            <el-option label="发送中" value="SENDING" />
            <el-option label="发送失败" value="FAILED" />
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

      <!-- 通知列表 -->
      <el-table
        v-loading="loading"
        :data="notificationList"
        style="width: 100%"
        border
      >
        <el-table-column prop="title" label="通知标题" />
        <el-table-column prop="type" label="通知类型" width="120">
          <template #default="scope">
            {{ getTypeName(scope.row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="target" label="接收对象" width="120">
          <template #default="scope">
            {{ getTargetName(scope.row.target) }}
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
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

    <!-- 发送通知对话框 -->
    <el-dialog
      v-model="showSendDialog"
      title="发送通知"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="notificationFormRef"
        :model="notificationForm"
        :rules="notificationRules"
        label-width="100px"
      >
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="notificationForm.type" placeholder="请选择通知类型">
            <el-option label="考勤提醒" value="ATTENDANCE" />
            <el-option label="课程变更" value="COURSE" />
            <el-option label="请假审批" value="LEAVE" />
            <el-option label="系统通知" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收对象" prop="target">
          <el-select v-model="notificationForm.target" placeholder="请选择接收对象">
            <el-option label="所有学生" value="ALL" />
            <el-option label="指定课程" value="COURSE" />
            <el-option label="指定班级" value="CLASS" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="notificationForm.target === 'COURSE'"
          label="选择课程"
          prop="courseId"
        >
          <el-select v-model="notificationForm.courseId" placeholder="请选择课程">
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="notificationForm.target === 'CLASS'"
          label="选择班级"
          prop="classId"
        >
          <el-select v-model="notificationForm.classId" placeholder="请选择班级">
            <el-option
              v-for="class_ in classOptions"
              :key="class_.id"
              :label="class_.name"
              :value="class_.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="notificationForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="notificationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        <el-form-item label="模板" prop="templateId">
          <el-select v-model="notificationForm.templateId" placeholder="请选择模板">
            <el-option label="不使用模板" value="" />
            <el-option
              v-for="template in templateOptions"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSendDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            发送
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="通知详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="通知标题">{{ currentNotification.title }}</el-descriptions-item>
        <el-descriptions-item label="通知类型">{{ getTypeName(currentNotification.type) }}</el-descriptions-item>
        <el-descriptions-item label="接收对象">{{ getTargetName(currentNotification.target) }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ currentNotification.sendTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentNotification.status)">
            {{ getStatusText(currentNotification.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送人">{{ currentNotification.sender }}</el-descriptions-item>
        <el-descriptions-item label="通知内容" :span="2">
          {{ currentNotification.content }}
        </el-descriptions-item>
        <el-descriptions-item label="发送结果" :span="2">
          <el-table :data="currentNotification.results" style="width: 100%">
            <el-table-column prop="name" label="接收人" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">
                  {{ scope.row.status === 'SUCCESS' ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="备注" />
          </el-table>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Message,
  Search,
  Refresh
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  type: '',
  dateRange: [],
  status: ''
})

// 通知列表数据
const loading = ref(false)
const notificationList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 发送通知对话框
const showSendDialog = ref(false)
const submitting = ref(false)
const notificationFormRef = ref(null)
const notificationForm = reactive({
  type: '',
  target: '',
  courseId: '',
  classId: '',
  title: '',
  content: '',
  templateId: ''
})

// 表单验证规则
const notificationRules = {
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  target: [
    { required: true, message: '请选择接收对象', trigger: 'change' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// 课程选项
const courseOptions = ref([
  { id: 1, name: '高等数学' },
  { id: 2, name: '大学物理' }
])

// 班级选项
const classOptions = ref([
  { id: 1, name: '计算机1班' },
  { id: 2, name: '计算机2班' }
])

// 模板选项
const templateOptions = ref([
  { id: 1, name: '考勤提醒模板' },
  { id: 2, name: '课程变更模板' }
])

// 通知详情对话框
const showDetailDialog = ref(false)
const currentNotification = ref({})

// 获取通知类型名称
const getTypeName = (type) => {
  const types = {
    'ATTENDANCE': '考勤提醒',
    'COURSE': '课程变更',
    'LEAVE': '请假审批',
    'SYSTEM': '系统通知'
  }
  return types[type] || type
}

// 获取接收对象名称
const getTargetName = (target) => {
  const targets = {
    'ALL': '所有学生',
    'COURSE': '指定课程',
    'CLASS': '指定班级'
  }
  return targets[target] || target
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'SENT': 'success',
    'SENDING': 'warning',
    'FAILED': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态显示文本
const getStatusText = (status) => {
  const texts = {
    'SENT': '已发送',
    'SENDING': '发送中',
    'FAILED': '发送失败'
  }
  return texts[status] || status
}

// 获取通知列表
async function fetchNotifications() {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    const res = await getAllNotifications(params, searchForm)
    if (res.code === 200) {
      notificationList.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取通知列表失败')
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNotifications()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    type: '',
    dateRange: [],
    status: ''
  })
  handleSearch()
}

// 发送通知
const handleSendNotification = () => {
  showSendDialog.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!notificationFormRef.value) return
  await notificationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用发送通知API
        ElMessage.success('发送成功')
        showSendDialog.value = false
        fetchNotifications()
      } catch (error) {
        ElMessage.error('发送失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 查看详情
const handleViewDetail = (row) => {
  currentNotification.value = row
  showDetailDialog.value = true
}

// 删除通知
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该通知吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // TODO: 调用删除通知API
    ElMessage.success('删除成功')
    fetchNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchNotifications()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchNotifications()
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notifications-container {
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