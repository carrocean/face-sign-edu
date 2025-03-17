<template>
  <div class="courses-container">
    <el-button type="primary" @click="openCourseForm">添加课程</el-button>
    <CourseTable @edit-course="openCourseForm" @delete-course="delCourse"/>
    <CourseForm v-if="showForm" :course="selectedCourse" @save="saveCourse" @cancel="closeCourseForm"/>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import CourseTable from '@/components/teacher/CourseTable.vue';
import CourseForm from '@/components/teacher/CourseForm.vue';
import {deleteCourse, getCourseById} from '@/api/course.js';
import {ElMessage} from "element-plus";

const showForm = ref(false);
const selectedCourse = ref(null);

function openCourseForm(course = null) {
  if (course) {
    selectedCourse.value = course;
  } else {
    selectedCourse.value = null;
  }
  showForm.value = true;
}

function closeCourseForm() {
  showForm.value = false;
}

function saveCourse(course) {
  // 调用 API 保存课程信息
  console.log('保存课程信息:', course);
  closeCourseForm();
}

async function delCourse(courseId) {
  deleteCourse(courseId)
      .then((res) => {
        if (res.code === 200) {
          ElMessage.success("课程删除成功");
        } else {
          ElMessage.error(res.message);
        }
      })
      .catch((err) => {
        // 请求失败，做相应处理
        console.log(err);
      });
}
</script>

<style scoped>
.courses-container {
  padding: 20px;
}
</style>