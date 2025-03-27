<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <el-button type="primary" @click="handleAddCourse">添加课程</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="searchForm.semester" placeholder="请选择学期">
            <el-option
              v-for="item in semesterOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="schedule" label="上课时间" width="180" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleViewStudents(scope.row)">学生名单</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加课程' : '编辑课程'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="courseForm.semester" placeholder="请选择学期">
            <el-option
              v-for="item in semesterOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="courseForm.schedule" />
        </el-form-item>
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="courseForm.location" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 学生名单对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      title="学生名单"
      width="800px"
    >
      <el-table :data="studentList" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="attendanceRate" label="出勤率" width="100">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.attendanceRate"
              :status="getProgressStatus(scope.row.attendanceRate)"
            />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="studentDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  semester: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const courseList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 学期选项
const semesterOptions = [
  { label: '2023-2024-1', value: '2023-2024-1' },
  { label: '2023-2024-2', value: '2023-2024-2' }
]

// 状态选项
const statusOptions = [
  { label: '进行中', value: 'active' },
  { label: '已结束', value: 'ended' }
]

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const submitting = ref(false)
const formRef = ref(null)
const courseForm = reactive({
  name: '',
  semester: '',
  schedule: '',
  location: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  schedule: [
    { required: true, message: '请输入上课时间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
  ]
}

// 学生名单相关
const studentDialogVisible = ref(false)
const studentList = ref([])

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

// 获取进度条状态
const getProgressStatus = (rate) => {
  if (rate >= 90) return 'success'
  if (rate >= 80) return 'warning'
  return 'exception'
}

// 搜索
const handleSearch = () => {
  fetchCourseList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.semester = ''
  searchForm.status = ''
  handleSearch()
}

// 获取课程列表
const fetchCourseList = () => {
  loading.value = true
  // TODO: 调用后端API获取课程列表
  // 模拟数据
  setTimeout(() => {
    courseList.value = [
      {
        id: 1,
        name: '高等数学',
        semester: '2023-2024-1',
        schedule: '周一 1-2节',
        location: '教学楼A101',
        studentCount: 60,
        status: 'active'
      },
      {
        id: 2,
        name: '大学物理',
        semester: '2023-2024-1',
        schedule: '周二 3-4节',
        location: '教学楼B202',
        studentCount: 45,
        status: 'active'
      }
    ]
    total.value = 2
    loading.value = false
  }, 500)
}

// 添加课程
const handleAddCourse = () => {
  dialogType.value = 'add'
  Object.assign(courseForm, {
    name: '',
    semester: '',
    schedule: '',
    location: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑课程
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(courseForm, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用后端API保存课程信息
        await new Promise(resolve => setTimeout(resolve, 1000))
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
        dialogVisible.value = false
        fetchCourseList()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 查看学生名单
const handleViewStudents = (course) => {
  // TODO: 调用后端API获取学生名单
  // 模拟数据
  studentList.value = [
    {
      studentId: '2024001',
      name: '张三',
      college: '计算机学院',
      major: '软件工程',
      grade: '2024级',
      attendanceRate: 95
    },
    {
      studentId: '2024002',
      name: '李四',
      college: '计算机学院',
      major: '软件工程',
      grade: '2024级',
      attendanceRate: 85
    }
  ]
  studentDialogVisible.value = true
}

// 删除课程
const handleDelete = (course) => {
  ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端API删除课程
      await new Promise(resolve => setTimeout(resolve, 1000))
      ElMessage.success('删除成功')
      fetchCourseList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
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