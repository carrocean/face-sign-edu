<template>
  <el-dialog
    v-model="dialogVisible"
    title="数据导出"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="exportForm" label-width="120px">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="exportForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      
      <el-form-item label="文件格式">
        <el-radio-group v-model="exportForm.format">
          <el-radio label="excel">Excel</el-radio>
          <el-radio label="pdf">PDF</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exporting">
          开始导出
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch} from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    required: true,
    validator: (value) => ['attendance', 'leave'].includes(value)
  },
  exportFunction: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'export-success'])

const dialogVisible = ref(props.visible)
const exporting = ref(false)
const exportForm = ref({
  dateRange: [],
  format: 'excel'
})

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

// 监听对话框关闭
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 处理导出
const handleExport = async () => {
  if (!exportForm.value.dateRange || exportForm.value.dateRange.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }

  exporting.value = true
  try {
    const params = {
      startDate: exportForm.value.dateRange[0],
      endDate: exportForm.value.dateRange[1],
      format: exportForm.value.format
    }

    const res = await props.exportFunction(params)

    // 创建下载链接
    const blob = new Blob([res.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${props.type === 'attendance' ? '考勤记录' : '请假记录'}_${new Date().getTime()}.${exportForm.value.format}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
    emit('export-success')
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 