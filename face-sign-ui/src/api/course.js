// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/course/';
var courseUrl = {
    getAllCourses: prefix + 'list', // 获取所有课程
    getCourseById: prefix + 'getById', // 根据ID获取课程详情
    saveCourse: prefix + 'save', // 添加新课程
    updateCourse: prefix + 'update', // 更新课程
    deleteCourse: prefix + 'delete' // 删除课程
};

// 获取所有课程
export function getAllCourses(params, data) {
    return request({
        url: courseUrl.getAllCourses,
        method: 'post',
        params: params,
        data: data
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

export class getStudentCourses {
}