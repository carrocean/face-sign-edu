// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/student/';
var studentUrl = {
    getAllStudents: prefix + 'list', // 获取所有学生
    getAllPageStudents: prefix + 'page-list', // 条件分页查询
    getStudentById: prefix + 'getById', // 根据ID获取学生详情
    saveStudent: prefix + 'save', // 添加新学生
    addStudent: prefix + 'add', // 新增学生的同时新增用户
    updateStudent: prefix + 'update', // 更新学生
    deleteStudent: prefix + 'delete', // 删除学生
    getStudentByUserId: prefix + 'getByUserId', // 根据用户ID获取学生详情
    batchDeleteStudents: prefix + 'delete/batch' // 批量删除用户
};

// 条件分页查询
export function getAllPageStudents(params, data) {
    return request({
        url: studentUrl.getAllPageStudents,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有学生
export function getAllStudents() {
    return request({
        url: studentUrl.getAllStudents,
        method: 'get'
    });
}

// 根据ID获取学生详情
export function getStudentById(studentId) {
    return request({
        url: studentUrl.getStudentById + '/' + studentId,
        method: 'get'
    });
}

// 根据用户ID获取学生详情
export function getStudentByUserId(userId) {
    return request({
        url: studentUrl.getStudentByUserId + '/' + userId,
        method: 'get'
    });
}

// 添加新学生
export function saveStudent(data) {
    return request({
        url: studentUrl.saveStudent,
        method: 'post',
        data: data
    });
}

// 新增学生的同时新增用户
export function addStudent(data) {
    return request({
        url: studentUrl.addStudent,
        method: 'post',
        data: data
    });
}

// 更新学生
export function updateStudent(data) {
    return request({
        url: studentUrl.updateStudent,
        method: 'put',
        data: data
    });
}

// 删除学生
export function deleteStudent(studentId) {
    return request({
        url: studentUrl.deleteStudent + '/' + studentId,
        method: 'delete'
    });
}

export function batchDeleteStudents(ids) {
    return request({
        url: studentUrl.batchDeleteStudents,
        method: 'delete',
        data: ids
    });
}