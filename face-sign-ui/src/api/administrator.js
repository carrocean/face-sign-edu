// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/administrator/';
var administratorUrl = {
    getAllAdministrators: prefix + 'list', // 获取所有管理员
    getAdministratorById: prefix + 'getById', // 根据ID获取管理员详情
    saveAdministrator: prefix + 'save', // 添加新管理员
    updateAdministrator: prefix + 'update', // 更新管理员
    deleteAdministrator: prefix + 'delete' // 删除管理员
};

// 获取所有管理员
export function getAllAdministrators(params, data) {
    return request({
        url: administratorUrl.getAllAdministrators,
        method: 'post',
        params: params,
        data: data
    });
}

// 根据ID获取管理员详情
export function getAdministratorById(administratorId) {
    return request({
        url: administratorUrl.getAdministratorById + '/' + administratorId,
        method: 'get'
    });
}

// 添加新管理员
export function saveAdministrator(data) {
    return request({
        url: administratorUrl.saveAdministrator,
        method: 'post',
        data: data
    });
}

// 更新管理员
export function updateAdministrator(data) {
    return request({
        url: administratorUrl.updateAdministrator,
        method: 'put',
        data: data
    });
}

// 删除管理员
export function deleteAdministrator(administratorId) {
    return request({
        url: administratorUrl.deleteAdministrator + '/' + administratorId,
        method: 'delete'
    });
}