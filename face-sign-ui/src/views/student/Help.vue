<template>
  <div class="help-container">
    <el-card class="help-card">
      <template #header>
        <div class="card-header">
          <span>帮助中心</span>
        </div>
      </template>

      <!-- 搜索框 -->
      <div class="search-container">
        <el-input
          v-model="searchQuery"
          placeholder="搜索问题"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
        />
      </div>

      <!-- 常见问题 -->
      <div class="faq-section">
        <h3>常见问题</h3>
        <el-collapse v-model="activeFaq">
          <el-collapse-item
            v-for="faq in faqs"
            :key="faq.id"
            :title="faq.question"
            :name="faq.id"
          >
            <div class="faq-answer">{{ faq.answer }}</div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <el-divider />

      <!-- 使用指南 -->
      <div class="guide-section">
        <h3>使用指南</h3>
        <el-timeline>
          <el-timeline-item
            v-for="guide in guides"
            :key="guide.id"
            :timestamp="guide.title"
            placement="top"
          >
            <el-card>
              <h4>{{ guide.subtitle }}</h4>
              <p>{{ guide.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <el-divider />

      <!-- 联系支持 -->
      <div class="contact-section">
        <h3>联系支持</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card class="contact-card">
              <template #header>
                <div class="contact-header">
                  <el-icon><Service /></el-icon>
                  <span>在线客服</span>
                </div>
              </template>
              <div class="contact-content">
                <p>工作时间：周一至周五 9:00-18:00</p>
                <p>服务热线：400-123-4567</p>
                <el-button type="primary" @click="contactService">
                  联系客服
                </el-button>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="contact-card">
              <template #header>
                <div class="contact-header">
                  <el-icon><Message /></el-icon>
                  <span>问题反馈</span>
                </div>
              </template>
              <div class="contact-content">
                <p>如果您在使用过程中遇到问题，可以通过以下方式反馈：</p>
                <el-button type="primary" @click="showFeedbackForm = true">
                  提交反馈
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 反馈表单对话框 -->
    <el-dialog
      v-model="showFeedbackForm"
      title="问题反馈"
      width="500px"
    >
      <el-form
        ref="feedbackFormRef"
        :model="feedbackForm"
        :rules="feedbackRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="feedbackForm.title" placeholder="请输入反馈标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型">
            <el-option label="功能问题" value="bug" />
            <el-option label="使用建议" value="suggestion" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您遇到的问题或建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFeedbackForm = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Service,
  Message
} from '@element-plus/icons-vue'

// 搜索相关
const searchQuery = ref('')
const handleSearch = (value) => {
  // TODO: 实现搜索功能
  console.log('搜索:', value)
}

// 常见问题
const activeFaq = ref(['1'])
const faqs = ref([
  {
    id: '1',
    question: '如何进行人脸考勤？',
    answer: '点击左侧菜单的"人脸考勤"选项，确保摄像头正常工作，按照提示完成人脸识别即可完成考勤。'
  },
  {
    id: '2',
    question: '如何申请请假？',
    answer: '点击左侧菜单的"请假管理"，选择"新建请假"，填写请假信息并提交即可。'
  },
  {
    id: '3',
    question: '如何查看考勤记录？',
    answer: '点击左侧菜单的"考勤记录"，可以查看所有课程的考勤情况，支持按日期和课程筛选。'
  }
])

// 使用指南
const guides = ref([
  {
    id: 1,
    title: '第一步：登录系统',
    subtitle: '账号登录',
    content: '使用学号和密码登录系统，首次登录请修改默认密码。'
  },
  {
    id: 2,
    title: '第二步：完善信息',
    subtitle: '个人信息设置',
    content: '在"个人信息"页面完善个人资料，上传人脸照片用于考勤。'
  },
  {
    id: 3,
    title: '第三步：开始使用',
    subtitle: '功能使用',
    content: '查看课程信息，进行考勤打卡，申请请假等操作。'
  }
])

// 反馈表单
const showFeedbackForm = ref(false)
const feedbackFormRef = ref(null)
const feedbackForm = reactive({
  title: '',
  type: '',
  content: ''
})

const feedbackRules = {
  title: [{ required: true, message: '请输入反馈标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  await feedbackFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用后端API提交反馈
      ElMessage.success('反馈提交成功')
      showFeedbackForm.value = false
    }
  })
}

// 联系客服
const contactService = () => {
  // TODO: 实现联系客服功能
  ElMessage.info('正在连接客服...')
}
</script>

<style scoped>
.help-container {
  padding: 20px;
}

.help-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  margin-bottom: 30px;
}

.faq-section,
.guide-section,
.contact-section {
  margin-bottom: 30px;
}

.faq-section h3,
.guide-section h3,
.contact-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.faq-answer {
  line-height: 1.6;
  color: #666;
}

.contact-card {
  height: 100%;
}

.contact-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-content {
  text-align: center;
  padding: 20px 0;
}

.contact-content p {
  margin-bottom: 15px;
  color: #666;
}

.el-divider {
  margin: 30px 0;
}
</style> 