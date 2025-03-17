<template>
  <el-table :data="courses" style="width: 100%">
    <el-table-column prop="courseName" label="课程名称"></el-table-column>
    <el-table-column prop="teacherName" label="授课教师"></el-table-column>
    <el-table-column label="操作">
      <template #default="scope">
        <el-button type="text" @click="$emit('edit-course', scope.row)">编辑</el-button>
        <el-button type="text" @click="$emit('delete-course', scope.row.courseId)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllCourses } from '@/api/course.js';

const courses = ref([]);

onMounted(async () => {
  courses.value = await getAllCourses();
});
</script>