import router from '@/router/index'
import common from "@/libs/globalFunction/common.js";
import config from "@/config/index.js";

// 路由全局前置守卫
router.beforeEach((to, from, next) => {
    // 检查cookie中的token
    const token = common.getCookies(config.tokenKeyName); // 确保你有一个函数来获取cookie中的token
    const userRole = common.getCookies(config.userRole)

    if (to.path === '/login' || to.path === '/register') {
        next()
        return
    }

    if (!token) {
        next('/login')
        return
    }

    // 根据用户角色限制访问路径
    if (userRole === 'admin' && !to.path.startsWith('/admin')) {
        next('/admin/dashboard')
    } else if (userRole === 'teacher' && !to.path.startsWith('/teacher')) {
        next('/teacher/dashboard')
    } else if (userRole === 'student' && !to.path.startsWith('/student')) {
        next('/student/dashboard')
    } else {
        next()
    }
});
