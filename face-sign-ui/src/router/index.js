import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/Admin.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/Users.vue')
        },
        {
          path: 'courses',
          name: 'admin-courses',
          component: () => import('../views/admin/Courses.vue')
        },
        {
          path: 'profile',
          name: 'admin-profile',
          component: () => import('../views/admin/Profile.vue')
        }
      ]
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: () => import('../views/teacher/Teacher.vue'),
      redirect: '/teacher/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'teacher-dashboard',
          component: () => import('../views/teacher/Dashboard.vue')
        },
        {
          path: 'courses',
          name: 'teacher-courses',
          component: () => import('../views/teacher/Courses.vue')
        },
        {
          path: 'attendance',
          name: 'teacher-attendance',
          component: () => import('../views/teacher/Attendance.vue')
        },
        {
          path: 'profile',
          name: 'teacher-profile',
          component: () => import('../views/teacher/Profile.vue')
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: () => import('../views/student/Student.vue'),
      redirect: '/student/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'student-dashboard',
          component: () => import('../views/student/Dashboard.vue')
        },
        {
          path: 'courses',
          name: 'student-courses',
          component: () => import('../views/student/Courses.vue')
        },
        {
          path: 'attendance',
          name: 'student-attendance',
          component: () => import('../views/student/Attendance.vue')
        },
        {
          path: 'profile',
          name: 'student-profile',
          component: () => import('../views/student/Profile.vue')
        },
        {
          path: 'face-test',
          component: () => import('../views/student/FaceTest.vue')
        }
      ]
    }
  ]
})

export default router
