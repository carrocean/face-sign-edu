// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/course-schedule/';
var courseScheduleUrl = {
    getAllCourseSchedules: prefix + 'list', // 获取所有课程安排
    getCourseScheduleById: prefix + 'getById', // 根据ID获取课程安排详情
    saveCourseSchedule: prefix + 'save', // 添加新课程安排
    updateCourseSchedule: prefix + 'update', // 更新课程安排
    deleteCourseSchedule: prefix + 'delete' // 删除课程安排
};

// 获取所有课程安排
export function getAllCourseSchedules(params, data) {
    return request({
        url: courseScheduleUrl.getAllCourseSchedules,
        method: 'post',
        params: params,
        data: data
    });
}

// 根据ID获取课程安排详情
export function getCourseScheduleById(courseScheduleId) {
    return request({
        url: courseScheduleUrl.getCourseScheduleById + '/' + courseScheduleId,
        method: 'get'
    });
}

// 添加新课程安排
export function saveCourseSchedule(data) {
    return request({
        url: courseScheduleUrl.saveCourseSchedule,
        method: 'post',
        data: data
    });
}

// 更新课程安排
export function updateCourseSchedule(data) {
    return request({
        url: courseScheduleUrl.updateCourseSchedule,
        method: 'put',
        data: data
    });
}

// 删除课程安排
export function deleteCourseSchedule(courseScheduleId) {
    return request({
        url: courseScheduleUrl.deleteCourseSchedule + '/' + courseScheduleId,
        method: 'delete'
    });
}