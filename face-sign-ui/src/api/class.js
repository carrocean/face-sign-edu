// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/class/';
var classUrl = {
    getAllClasses: prefix + 'list', // 获取所有班级
    getClassById: prefix + 'getById', // 根据ID获取班级详情
    saveClass: prefix + 'save', // 添加新班级
    updateClass: prefix + 'update', // 更新班级
    deleteClass: prefix + 'delete' // 删除班级
};

// 获取所有班级
export function getAllClasses(params, data) {
    return request({
        url: classUrl.getAllClasses,
        method: 'post',
        params: params,
        data: data
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