// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/administrator/';
var administratorUrl = {
    getAllAdministrators: prefix + 'list', // 获取所有管理员
    getAllPageAdministrators: prefix + 'page-list', // 条件分页查询
    getAdministratorById: prefix + 'getById', // 根据ID获取管理员详情
    saveAdministrator: prefix + 'save', // 添加新管理员
    updateAdministrator: prefix + 'update', // 更新管理员
    deleteAdministrator: prefix + 'delete', // 删除管理员
    batchDeleteAdministrators: prefix + 'delete/batch', // 批量删除管理员
    getAdministratorByUserId: prefix + 'getByUserId' // 根据用户ID获取管理员详情
};

// 条件分页查询
export function getAllPageAdministrators(params, data) {
    return request({
        url: administratorUrl.getAllPageAdministrators,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有管理员
export function getAllAdministrators() {
    return request({
        url: administratorUrl.getAllAdministrators,
        method: 'get'
    });
}

// 根据ID获取管理员详情
export function getAdministratorById(administratorId) {
    return request({
        url: administratorUrl.getAdministratorById + '/' + administratorId,
        method: 'get'
    });
}

// 根据用户ID获取管理员详情
export function getAdministratorByUserId(userId) {
    return request({
        url: administratorUrl.getAdministratorByUserId + '/' + userId,
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

// 批量删除管理员
export function batchDeleteAdministrators(ids) {
    return request({
        url: administratorUrl.batchDeleteAdministrators,
        method: 'delete',
        data: ids
    });
}