// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/teacher/';
var teacherUrl = {
    getAllTeachers: prefix + 'list', // 获取所有教师
    getAllPageTeachers: prefix + 'page-list', // 条件分页查询
    getTeacherById: prefix + 'getById', // 根据ID获取教师详情
    saveTeacher: prefix + 'save', // 添加新教师
    updateTeacher: prefix + 'update', // 更新教师
    deleteTeacher: prefix + 'delete', // 删除教师
    batchDeleteTeachers: prefix + 'delete/batch', // 批量删除教师
    getTeacherByUserId: prefix + 'getByUserId' // 根据用户ID获取教师详情
};

// 条件分页查询
export function getAllPageTeachers(params, data) {
    return request({
        url: teacherUrl.getAllPageTeachers,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有教师
export function getAllTeachers() {
    return request({
        url: teacherUrl.getAllTeachers,
        method: 'get'
    });
}

// 根据ID获取教师详情
export function getTeacherById(teacherId) {
    return request({
        url: teacherUrl.getTeacherById + '/' + teacherId,
        method: 'get'
    });
}

// 根据用户ID获取教师详情
export function getTeacherByUserId(userId) {
    return request({
        url: teacherUrl.getTeacherByUserId + '/' + userId,
        method: 'get'
    });
}

// 添加新教师
export function saveTeacher(data) {
    return request({
        url: teacherUrl.saveTeacher,
        method: 'post',
        data: data
    });
}

// 更新教师
export function updateTeacher(data) {
    return request({
        url: teacherUrl.updateTeacher,
        method: 'put',
        data: data
    });
}

// 删除教师
export function deleteTeacher(teacherId) {
    return request({
        url: teacherUrl.deleteTeacher + '/' + teacherId,
        method: 'delete'
    });
}

// 批量删除教师
export function batchDeleteTeachers(ids) {
    return request({
        url: teacherUrl.batchDeleteTeachers,
        method: 'delete',
        data: ids
    });
}