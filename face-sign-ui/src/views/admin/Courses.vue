<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">添加课程</el-button>
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
        <el-table-column prop="studentCount" label="学生人数" />
        <el-table-column prop="schedule" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
              {{ scope.row.status === 'active' ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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
      width="500px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="courseForm.teacherId" placeholder="请选择教师" filterable>
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="courseForm.schedule" placeholder="请输入上课时间" />
        </el-form-item>
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="courseForm.location" placeholder="请输入上课地点" />
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-select v-model="courseForm.status" placeholder="请选择状态">
            <el-option label="进行中" value="active" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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
const dialogType = ref('add')
const courseFormRef = ref(null)
const courseForm = reactive({
  name: '',
  teacherId: '',
  schedule: '',
  location: '',
  status: 'active'
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  schedule: [
    { required: true, message: '请输入上课时间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择课程状态', trigger: 'change' }
  ]
}

// 获取教师列表
const fetchTeacherList = () => {
  // TODO: 调用后端API获取教师列表
  // 模拟数据
  teacherList.value = [
    { id: 1, name: '张老师' },
    { id: 2, name: '李老师' },
    { id: 3, name: '王老师' }
  ]
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
const fetchCourseList = () => {
  loading.value = true
  // TODO: 调用后端API获取课程列表
  // 模拟数据
  setTimeout(() => {
    courseList.value = [
      {
        id: 1,
        name: '高等数学',
        teacherName: '张老师',
        studentCount: 50,
        schedule: '周一 1-2节',
        location: '教学楼A101',
        status: 'active'
      },
      {
        id: 2,
        name: '大学物理',
        teacherName: '李老师',
        studentCount: 45,
        schedule: '周二 3-4节',
        location: '教学楼B202',
        status: 'active'
      }
    ]
    total.value = 2
    loading.value = false
  }, 500)
}

// 添加课程
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(courseForm, {
    name: '',
    teacherId: '',
    schedule: '',
    location: '',
    status: 'active'
  })
}

// 编辑课程
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(courseForm, row)
}

// 删除课程
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该课程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用后端API删除课程
    ElMessage.success('删除成功')
    fetchCourseList()
  })
}

// 提交表单
const handleSubmit = () => {
  courseFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用后端API保存课程
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      fetchCourseList()
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
onMounted(() => {
  fetchTeacherList()
  fetchCourseList()
})
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