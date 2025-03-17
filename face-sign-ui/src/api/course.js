// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/courses';
var courseUrl = {
    getAllCourses: prefix, // 获取所有课程
    getCourseById: prefix + 'getCourseById', // 根据ID获取课程详情
    saveCourse: prefix, // 添加新课程
    updateCourse: prefix + 'updateCourse', // 更新课程
    deleteCourse: prefix + 'deleteCourse' // 删除课程
};

// 获取所有课程
export function getAllCourses() {
    return request({
        url: courseUrl.getAllCourses,
        method: 'get'
    });
}

// 根据ID获取课程详情
export function getCourseById(courseId) {
    return request({
        url: courseUrl.getCourseById + '/' + courseId,
        method: 'get'
    });
}

// 添加新课程
export function saveCourse(data) {
    return request({
        url: courseUrl.saveCourse,
        method: 'post',
        data: data
    });
}

// 更新课程
export function updateCourse(courseId, data) {
    return request({
        url: courseUrl.updateCourse + '/' + courseId,
        method: 'put',
        data: data
    });
}

// 删除课程
export function deleteCourse(courseId) {
    return request({
        url: courseUrl.deleteCourse + '/' + courseId,
        method: 'delete'
    });
}