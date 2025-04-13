<template>
  <div class="course-detail">
    <el-card class="course-info-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <span>课程信息</span>
          </div>
          <div class="header-buttons">
            <el-button type="primary" @click="handleEditCourse">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ courseInfo.courseName }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ getTeacherName(courseInfo.teacherId) }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ getClassName(courseInfo.classId) }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ courseInfo.semester }}</el-descriptions-item>
        <el-descriptions-item label="开课周数">{{ courseInfo.startWeek }} - {{ courseInfo.endWeek }}周</el-descriptions-item>
        <el-descriptions-item label="添加时间">{{ parseTime(courseInfo.addTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(courseInfo.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

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
            <el-switch
              v-model="editMode"
              size="large"
              inline-prompt
              active-text="编辑模式"
              inactive-text="查看模式"
              style="margin-right: 16px;"
            />
            <el-button 
              type="primary" 
              @click="handleAddSchedule"
              v-if="editMode"
            >
              <el-icon><Plus/></el-icon>
              添加课程安排
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
        <el-table-column v-for="day in 7" :key="day" :label="`星期${day}`" align="center">
          <template #default="{ row }">
            <div class="schedule-cell">
              <template v-if="getScheduleByDayAndPeriod(day, row.period)">
                <div class="schedule-content">
                  <div class="schedule-classroom">{{ getScheduleByDayAndPeriod(day, row.period).classroom }}</div>
                  <div class="schedule-actions" v-if="editMode">
                    <el-button type="primary" link @click="handleEditSchedule(getScheduleByDayAndPeriod(day, row.period))">
                      修改
                    </el-button>
                    <el-button type="danger" link @click="handleDeleteSchedule(getScheduleByDayAndPeriod(day, row.period))">
                      删除
                    </el-button>
                  </div>
                </div>
              </template>
              <template v-else>
                <el-button 
                  type="primary" 
                  link 
                  @click="handleAddSchedule(day, row.period)"
                  v-if="editMode"
                >
                  添加
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑课程对话框 -->
    <el-dialog
      v-model="editCourseDialogVisible"
      title="编辑课程"
      width="500px"
    >
      <el-form :model="editCourseForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="editCourseForm.courseName" />
        </el-form-item>
        <el-form-item label="授课教师">
          <el-select v-model="editCourseForm.teacherId" placeholder="请选择教师">
            <el-option
              v-for="teacher in teacherOptions"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="editCourseForm.classId" placeholder="请选择班级">
            <el-option
              v-for="classItem in classOptions"
              :key="classItem.id"
              :label="classItem.className"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="editCourseForm.semester" />
        </el-form-item>
        <el-form-item label="开课周数">
          <el-input-number v-model="editCourseForm.startWeek" :min="1" />
          <span class="mx-2">-</span>
          <el-input-number v-model="editCourseForm.endWeek" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editCourseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditCourse">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑课程安排对话框 -->
    <el-dialog
      v-model="scheduleDialogVisible"
      :title="scheduleDialogType === 'add' ? '添加课程安排' : '修改课程安排'"
      width="500px"
    >
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="课程ID">
          <el-input v-model="courseId" disabled />
        </el-form-item>
        <el-form-item label="开始周数">
          <el-input-number 
            v-model="scheduleForm.startWeek" 
            :min="courseInfo.startWeek" 
            :max="courseInfo.endWeek"
            :disabled="scheduleDialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="结束周数">
          <el-input-number 
            v-model="scheduleForm.endWeek" 
            :min="scheduleForm.startWeek" 
            :max="courseInfo.endWeek"
            :disabled="scheduleDialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="星期">
          <el-input-number 
            v-model="scheduleForm.weekDay" 
            :min="1" 
            :max="7" 
            :disabled="scheduleDialogType === 'edit'" 
          />
        </el-form-item>
        <el-form-item label="节次">
          <el-input-number 
            v-model="scheduleForm.period" 
            :min="1" 
            :max="4" 
            :disabled="scheduleDialogType === 'edit'" 
          />
        </el-form-item>
        <el-form-item label="上课地点">
          <el-input v-model="scheduleForm.classroom" placeholder="请输入上课地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSchedule">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted, reactive} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { parseTime } from '@/utils/Utils.js'
import { getCourseById, updateCourse } from '@/api/course'
import { getAllTeachers } from '@/api/teacher'
import { getAllClasses } from '@/api/class'
import { 
  getAllPageCourseSchedules, 
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
  {period: '第1节', time: '08:00-09:40'},
  {period: '第2节', time: '10:00-11:40'},
  {period: '第3节', time: '14:30-16:05'},
  {period: '第4节', time: '16:25-18:00'}
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
  if (!courseInfo.value.startWeek || !courseInfo.value.endWeek) return []
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

// 获取课程信息
async function fetchCourseInfo() {
  try {
    loading.value = true
    const res = await getCourseById(courseId)
    courseInfo.value = res.data
    editCourseForm.value = { ...res.data }
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  } finally {
    loading.value = false
  }
}

// 获取课程安排
async function fetchCourseSchedules() {
  try {
    loading.value = true
    let pageParams = reactive({
      pageSearch: false,
      fuzzySearch: true  // 默认启用模糊查询
    })

    let searchForm = reactive({
      courseId: courseId,
      theWeek: currentWeek.value
    })

    const res = await getAllPageCourseSchedules(pageParams, searchForm)
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
    const [teacherRes, classRes] = await Promise.all([
      getAllTeachers(),
      getAllClasses()
    ])
    teacherOptions.value = teacherRes.data
    classOptions.value = classRes.data
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

// 提交编辑课程
async function submitEditCourse() {
  try {
    await updateCourse(courseId, editCourseForm.value)
    ElMessage.success('更新成功')
    editCourseDialogVisible.value = false
    fetchCourseInfo()
  } catch (error) {
    console.error('更新课程失败:', error)
    ElMessage.error('更新课程失败')
  }
}

// 添加课程安排
function handleAddSchedule(day, period) {
  scheduleDialogType.value = 'add'
  scheduleForm.value = {
    startWeek: currentWeek.value,
    endWeek: currentWeek.value,
    weekDay: day || 1,
    period: period ? periods.findIndex(p => p.period === period) + 1 : 1,
    classroom: ''
  }
  scheduleDialogVisible.value = true
}

// 修改课程安排
function handleEditSchedule(schedule) {
  scheduleDialogType.value = 'edit'
  scheduleForm.value = { ...schedule }
  scheduleDialogVisible.value = true
}

// 删除课程安排
async function handleDeleteSchedule(schedule) {
  try {
    await ElMessageBox.confirm('确定要删除该课程安排吗？', '提示', {
      type: 'warning'
    })
    await deleteCourseSchedule(schedule.id)
    ElMessage.success('删除成功')
    fetchCourseSchedules()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程安排失败:', error)
      ElMessage.error('删除课程安排失败')
    }
  }
}

// 提交课程安排
async function submitSchedule() {
  try {
    if (scheduleDialogType.value === 'add') {
      await addCourseSchedule({
        courseId,
        ...scheduleForm.value
      })
      ElMessage.success('添加成功')
    } else {
      await updateCourseSchedule(scheduleForm.value.id, scheduleForm.value)
      ElMessage.success('更新成功')
    }
    scheduleDialogVisible.value = false
    fetchCourseSchedules()
  } catch (error) {
    console.error(`${scheduleDialogType.value === 'add' ? '添加' : '更新'}课程安排失败:`, error)
    ElMessage.error(`${scheduleDialogType.value === 'add' ? '添加' : '更新'}课程安排失败`)
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 获取教师名称
function getTeacherName(teacherId) {
  const teacher = teacherOptions.value.find(t => t.id === teacherId)
  return teacher ? teacher.name : '-'
}

// 获取班级名称
function getClassName(classId) {
  const classItem = classOptions.value.find(c => c.id === classId)
  return classItem ? classItem.className : '-'
}

onMounted(() => {
  fetchCourseInfo()
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