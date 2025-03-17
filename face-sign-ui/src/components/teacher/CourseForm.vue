<template>
  <el-dialog :visible.sync="visible" title="课程信息">
    <el-form :model="course" label-width="120px">
      <el-form-item label="课程名称">
        <el-input v-model="course.courseName"></el-input>
      </el-form-item>
      <el-form-item label="授课教师">
        <el-input v-model="course.teacherName"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="primary" @click="$emit('save', course)">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch} from 'vue';

const props = defineProps({
  course: Object,
});

const visible = ref(false);
const course = ref({});

watch(() => props.course, (newVal) => {
  course.value = newVal || {};
});

defineExpose({
  visible,
});
</script>