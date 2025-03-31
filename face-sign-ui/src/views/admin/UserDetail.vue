<template>
  <div class="user-detail-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户详情</span>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <!-- 基本信息 -->
        <el-descriptions-item label="ID">{{ userDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ userDetail.account }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userDetail.name }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(userDetail.role)">
            {{ getRoleLabel(userDetail.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userDetail.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="userDetail.status === 1 ? 'success' : 'danger'">
            {{ userDetail.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(userDetail.addTime) }}
        </el-descriptions-item>

        <!-- 管理员特有信息 -->
        <template v-if="userDetail.role === 'ADMIN'">
          <el-descriptions-item label="管理员ID">{{ userDetail.adminId }}</el-descriptions-item>
          <el-descriptions-item label="最后登录时间">
            {{ parseTime(userDetail.lastLoginTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录IP">{{ userDetail.lastLoginIp }}</el-descriptions-item>
          <el-descriptions-item label="登录次数">{{ userDetail.loginCount }}</el-descriptions-item>
        </template>

        <!-- 教师特有信息 -->
        <template v-if="userDetail.role === 'TEACHER'">
          <el-descriptions-item label="教师ID">{{ userDetail.teacherId }}</el-descriptions-item>
          <el-descriptions-item label="所属院系">{{ userDetail.department }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ userDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="授课课程数">{{ userDetail.courseCount }}</el-descriptions-item>
          <el-descriptions-item label="学生总数">{{ userDetail.studentCount }}</el-descriptions-item>
        </template>

        <!-- 学生特有信息 -->
        <template v-if="userDetail.role === 'STUDENT'">
          <el-descriptions-item label="学号">{{ userDetail.studentId }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ userDetail.className }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ userDetail.major }}</el-descriptions-item>
          <el-descriptions-item label="年级">{{ userDetail.grade }}</el-descriptions-item>
          <el-descriptions-item label="考勤率">{{ userDetail.attendanceRate }}%</el-descriptions-item>
          <el-descriptions-item label="请假次数">{{ userDetail.leaveCount }}</el-descriptions-item>
        </template>

        <!-- 通用备注信息 -->
        <el-descriptions-item label="备注" :span="2">{{ userDetail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 教师特有信息：授课课程列表 -->
      <template v-if="userDetail.role === 'TEACHER'">
        <div class="section-title">授课课程</div>
        <el-table :data="userDetail.courses" style="width: 100%" border>
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="courseCode" label="课程代码" width="120" />
          <el-table-column prop="studentCount" label="学生人数" width="100" />
          <el-table-column prop="attendanceRate" label="考勤率" width="100">
            <template #default="scope">
              {{ scope.row.attendanceRate }}%
            </template>
          </el-table-column>
        </el-table>
      </template>

      <!-- 学生特有信息：考勤记录 -->
      <template v-if="userDetail.role === 'STUDENT'">
        <div class="section-title">最近考勤记录</div>
        <el-table :data="userDetail.attendanceRecords" style="width: 100%" border>
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'PRESENT' ? 'success' : 'danger'">
                {{ scope.row.status === 'PRESENT' ? '已签到' : '未签到' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserDetail } from '@/api/user'
import { parseTime } from '@/utils/Utils'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 用户详情数据
const userDetail = reactive({
  id: '',
  account: '',
  name: '',
  role: '',
  email: '',
  phone: '',
  status: 1,
  addTime: '',
  remark: '',
  // 管理员特有字段
  adminId: '',
  lastLoginTime: '',
  lastLoginIp: '',
  loginCount: 0,
  // 教师特有字段
  teacherId: '',
  department: '',
  title: '',
  courseCount: 0,
  studentCount: 0,
  courses: [],
  // 学生特有字段
  studentId: '',
  className: '',
  major: '',
  grade: '',
  attendanceRate: 0,
  leaveCount: 0,
  attendanceRecords: []
})

// 获取角色标签类型
const getRoleType = (role) => {
  const types = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return types[role] || 'info'
}

// 获取角色显示文本
const getRoleLabel = (role) => {
  const labels = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return labels[role] || role
}

// 获取用户详情
const fetchUserDetail = async () => {
  try {
    const userId = route.params.id
    const res = await getUserDetail(userId)
    if (res.code === 200) {
      Object.assign(userDetail, res.data)
    } else {
      ElMessage.error(res.message || '获取用户详情失败')
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  fetchUserDetail()
})
</script>

<style scoped>
.user-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style> 