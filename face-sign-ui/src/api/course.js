// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/course/';
var courseUrl = {
    getAllCourses: prefix + 'list', // 获取所有课程
    getAllPageCourses: prefix + 'page-list', // 条件分页查询
    getCourseById: prefix + 'getById', // 根据ID获取课程详情
    saveCourse: prefix + 'save', // 添加新课程
    updateCourse: prefix + 'update', // 更新课程
    deleteCourse: prefix + 'delete', // 删除课程
    batchDeleteCourses: prefix + 'delete/batch', // 批量删除课程
    exportCourses: prefix + 'export-course', // 导出课程

};

// 条件分页查询
export function getAllPageCourses(params, data) {
    return request({
        url: courseUrl.getAllPageCourses,
        method: 'post',
        params: params,
        data: data
    });
}

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
export function updateCourse(data) {
    return request({
        url: courseUrl.updateCourse,
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

// 批量删除课程
export function batchDeleteCourses(ids) {
    return request({
        url: courseUrl.batchDeleteCourses,
        method: 'delete',
        data: ids
    });
}

export function exportCourses(data) {
    return request({
        url: courseUrl.exportCourses,
        method: 'post',
        data: data,
        responseType: 'blob'
    });
}