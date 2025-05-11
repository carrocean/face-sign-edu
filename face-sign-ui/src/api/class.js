// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/class/';
var classUrl = {
    getAllClasses: prefix + 'list', // 获取所有班级
    getAllPageClasses: prefix + 'page-list', // 条件分页查询
    getClassById: prefix + 'getById', // 根据ID获取班级详情
    saveClass: prefix + 'save', // 添加新班级
    updateClass: prefix + 'update', // 更新班级
    deleteClass: prefix + 'delete', // 删除班级
    batchDeleteClasses: prefix + 'delete/batch', // 批量删除班级
    exportStudents: prefix + 'export-student', // 导出学生
};

// 条件分页查询
export function getAllPageClasses(params, data) {
    return request({
        url: classUrl.getAllPageClasses,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有班级
export function getAllClasses() {
    return request({
        url: classUrl.getAllClasses,
        method: 'get'
    });
}

// 根据ID获取班级详情
export function getClassById(classId) {
    return request({
        url: classUrl.getClassById + '/' + classId,
        method: 'get'
    });
}

// 添加新班级
export function saveClass(data) {
    return request({
        url: classUrl.saveClass,
        method: 'post',
        data: data
    });
}

// 更新班级
export function updateClass(data) {
    return request({
        url: classUrl.updateClass,
        method: 'put',
        data: data
    });
}

// 删除班级
export function deleteClass(classId) {
    return request({
        url: classUrl.deleteClass + '/' + classId,
        method: 'delete'
    });
}

// 批量删除班级
export function batchDeleteClasses(ids) {
    return request({
        url: classUrl.batchDeleteClasses,
        method: 'delete',
        data: ids
    });
}

export function exportStudents(data) {
    return request({
        url: classUrl.exportStudents,
        method: 'post',
        data: data,
        responseType: 'blob'
    });
}