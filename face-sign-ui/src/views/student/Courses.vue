<template>
  <div class="course-detail">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <span>课程安排</span>
            <el-select v-model="currentWeek" placeholder="选择周次" @change="handleWeekChange" style="width: 120px; margin-left: 16px;">
              <el-option
                v-for="week in weekOptions"
                :key="week"
                :label="`第${week}周`"
                :value="week"
              />
            </el-select>
          </div>
          <div class="right">
            <el-button @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="scheduleData"
        style="width: 100%"
        v-loading="loading"
        :cell-style="{ padding: '8px', height: '120px' }"
      >
        <el-table-column prop="period" label="节次" width="100" align="center"/>
        <el-table-column prop="time" label="时间" width="150" align="center"/>
        <el-table-column v-for="day in 7" :key="day" :label="getWeekdayName(day)" align="center">
          <template #default="{ row }">
            <div class="schedule-cell">
              <template v-if="getScheduleByDayAndPeriod(day, row.period)">
                <div class="schedule-content">
                  <div>{{ getScheduleByDayAndPeriod(day, row.period).classroom }}</div>
                  <div>{{ getCourseNameBySchedule(day, row.period) }}</div>
                  <div>{{ getTeacherNameBySchedule(day, row.period) }}</div>
                </div>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import {ref, computed, onMounted, reactive} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { parseTime } from '@/utils/Utils.js'
import { getAllCourses } from '@/api/course'
import { getAllTeachers } from '@/api/teacher'
import { getAllClasses } from '@/api/class'
import { 
  getCourseSchedulesByStudent, 
  addCourseSchedule, 
  updateCourseSchedule, 
  deleteCourseSchedule 
} from '@/api/courseSchedule.js'
import {ArrowLeft, Back, Edit, Plus} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id

// 编辑模式
const editMode = ref(false)

// 课程信息
const courseInfo = ref({})
const loading = ref(false)
const currentWeek = ref(1)

// 节次数据
const periods = [
  {period: 1, time: '08:00-09:40'},
  {period: 2, time: '10:00-11:40'},
  {period: 3, time: '14:30-16:05'},
  {period: 4, time: '16:25-18:00'}
]

// 课程表数据
const scheduleData = computed(() => {
  return periods.map(period => ({
    period: period.period,
    time: period.time
  }))
})

// 周次选项
const weekOptions = computed(() => {
  if (!courseInfo.value.startWeek || !courseInfo.value.endWeek) return [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]
  const options = []
  for (let i = courseInfo.value.startWeek; i <= courseInfo.value.endWeek; i++) {
    options.push(i)
  }
  return options
})

// 课程安排列表
const scheduleList = ref([])

// 教师和班级选项
const teacherOptions = ref([])
const classOptions = ref([])
const courseOptions = ref([])

// 对话框相关
const editCourseDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)
const scheduleDialogType = ref('add') // 'add' or 'edit'

// 表单数据
const editCourseForm = ref({
  courseName: '',
  teacherId: '',
  classId: '',
  semester: '',
  startWeek: 1,
  endWeek: 1
})

const scheduleForm = ref({
  startWeek: 1,
  endWeek: 1,
  weekDay: 1,
  period: 1,
  classroom: ''
})



// 获取课程安排
async function fetchCourseSchedules() {
  try {
    loading.value = true
    let pageParams = reactive({
      pageSearch: false,
      fuzzySearch: false  // 默认启用精准查询
    })

    let searchForm = reactive({
      theWeek: currentWeek.value
    })

    const res = await getCourseSchedulesByStudent(pageParams, searchForm)
    scheduleList.value = res.data.records
  } catch (error) {
    console.error('获取课程安排失败:', error)
    ElMessage.error('获取课程安排失败')
  } finally {
    loading.value = false
  }
}

// 获取教师和班级选项
async function fetchOptions() {
  try {
    const [teacherRes, classRes, courseRes] = await Promise.all([
      getAllTeachers(),
      getAllClasses(),
      getAllCourses()
    ])
    teacherOptions.value = teacherRes.data
    classOptions.value = classRes.data
    courseOptions.value = courseRes.data
  } catch (error) {
    console.error('获取选项失败:', error)
    ElMessage.error('获取选项失败')
  }
}

// 根据星期和节次获取课程安排
function getScheduleByDayAndPeriod(day, period) {
  const periodIndex = periods.findIndex(p => p.period === period) + 1
  return scheduleList.value.find(schedule => 
    schedule.weekDay === day && 
    schedule.period === periodIndex &&
    schedule.theWeek === currentWeek.value
  )
}

// 处理周次变化
function handleWeekChange() {
  fetchCourseSchedules()
}

// 编辑课程
function handleEditCourse() {
  editCourseDialogVisible.value = true
}

// 返回上一页
function goBack() {
  router.back()
}

// 获取教师名称
function getTeacherNameBySchedule(day, period) {
  const scheduleItem = getScheduleByDayAndPeriod(day, period)
  const courseItem = courseOptions.value.find(c => c.id === scheduleItem.courseId)
  const teacher = teacherOptions.value.find(t => t.id === courseItem.teacherId)
  return teacher ? teacher.name : '-'
}

// 获取课程名称
function getCourseNameBySchedule(day, period) {
  const scheduleItem = getScheduleByDayAndPeriod(day, period)
  const courseItem = courseOptions.value.find(c => c.id === scheduleItem.courseId)
  return courseItem ? courseItem.courseName : '-'
}

// 星期名称转换
const getWeekdayName = (day) => {
  return ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'][day - 1]
}

onMounted(() => {
  fetchCourseSchedules()
  fetchOptions()
})
</script>

<style scoped>
.course-detail {
  padding: 20px;
}

.course-info-card,
.schedule-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header .right {
  display: flex;
  align-items: center;
}

.schedule-cell {
  height: 100%;
  padding: 8px;
}

.schedule-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.schedule-classroom {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.schedule-actions {
  display: flex;
  gap: 8px;
}

:deep(.el-table__cell) {
  padding: 8px !important;
  height: 120px !important;
}

.mx-2 {
  margin: 0 8px;
}
</style>