// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/course-schedule/';
var courseScheduleUrl = {
    getAllCourseSchedules: prefix + 'list', // 获取所有课程表
    getAllPageCourseSchedules: prefix + 'page-list', // 条件分页查询
    getCourseScheduleById: prefix + 'getById', // 根据ID获取课程表详情
    saveCourseSchedule: prefix + 'save', // 添加新课程表
    updateCourseSchedule: prefix + 'update', // 更新课程表
    deleteCourseSchedule: prefix + 'delete', // 删除课程表
    batchDeleteCourseSchedules: prefix + 'delete/batch' // 批量删除课程表
};

// 条件分页查询
export function getAllPageCourseSchedules(params, data) {
    return request({
        url: courseScheduleUrl.getAllPageCourseSchedules,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有课程表
export function getAllCourseSchedules() {
    return request({
        url: courseScheduleUrl.getAllCourseSchedules,
        method: 'get'
    });
}

// 根据ID获取课程表详情
export function getCourseScheduleById(courseScheduleId) {
    return request({
        url: courseScheduleUrl.getCourseScheduleById + '/' + courseScheduleId,
        method: 'get'
    });
}

// 添加新课程表
export function saveCourseSchedule(data) {
    return request({
        url: courseScheduleUrl.saveCourseSchedule,
        method: 'post',
        data: data
    });
}

// 更新课程表
export function updateCourseSchedule(data) {
    return request({
        url: courseScheduleUrl.updateCourseSchedule,
        method: 'put',
        data: data
    });
}

// 删除课程表
export function deleteCourseSchedule(courseScheduleId) {
    return request({
        url: courseScheduleUrl.deleteCourseSchedule + '/' + courseScheduleId,
        method: 'delete'
    });
}

// 批量删除课程表
export function batchDeleteCourseSchedules(ids) {
    return request({
        url: courseScheduleUrl.batchDeleteCourseSchedules,
        method: 'delete',
        data: ids
    });
}