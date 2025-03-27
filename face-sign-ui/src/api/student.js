// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/student';
var studentUrl = {
    getAllStudents: prefix + '/list', // 获取所有学生
    getStudentById: prefix + '/getById', // 根据ID获取学生详情
    saveStudent: prefix + '/save', // 添加新学生
    updateStudent: prefix + '/update', // 更新学生
    deleteStudent: prefix + '/delete' // 删除学生
};

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

// 添加新学生
export function saveStudent(data) {
    return request({
        url: studentUrl.saveStudent,
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