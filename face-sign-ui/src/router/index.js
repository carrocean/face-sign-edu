import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect:'/login'
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
      path: '/teacher',
      name: 'teacher',
      component: () => import( '../views/teacher/Teacher.vue'),
      children: [
        {
          path: '/teacher/dashboard',
          name: 'dashboard',
          component: () => import('../views/teacher/Dashboard.vue')
        },
        {
          path: '/teacher/profile',
          name: 'profile',
          component: () => import('../views/teacher/Profile.vue')
        },
        {
          path: '/teacher/courses',
          name: 'courses',
          component: () => import('../views/teacher/Courses.vue')
        },
        {
          path: '/teacher/attendance',
          name: 'attendance',
          component: () => import('../views/teacher/Attendance.vue')
        },
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: () => import( '../views/student/Student.vue')
    },
  ]
})

export default router
