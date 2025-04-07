//导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/user/';
var userUrl = {
    login: prefix + 'login',
    register: prefix + 'register',
    checkUserLoginInfo: prefix + 'checkUserLoginInfo',
    getAllUsers: prefix + 'list', // 获取所有用户
    getUserById: prefix + 'getById', // 根据ID获取用户详情
    saveUser: prefix + 'save', // 添加新用户
    updateUser: prefix + 'update', // 更新用户
    deleteUser: prefix + 'delete', // 删除用户
    batchDelete: prefix + 'delete/batch' // 批量删除用户
}

export function login(data) {
    return request({
        url: userUrl.login,
        method: 'post',
        data:data,
    })
}

export function register(data) {
    return request({
        url: userUrl.register,
        method: 'post',
        data:data,
    })
}

export function checkUserLoginInfo() {
    return request({
        url: userUrl.checkUserLoginInfo,
        method: 'get',
    })
}

// 获取所有用户
export function getAllUsers(params, data) {
    return request({
        url: userUrl.getAllUsers,
        method: 'post',
        params: params,
        data: data
    });
}

// 根据ID获取用户详情
export function getUserById(userId) {
    return request({
        url: userUrl.getUserById + '/' + userId,
        method: 'get'
    });
}

// 添加新用户
export function saveUser(data) {
    return request({
        url: userUrl.saveUser,
        method: 'post',
        data: data
    });
}

// 更新用户
export function updateUser(data) {
    return request({
        url: userUrl.updateUser,
        method: 'put',
        data: data
    });
}

// 删除用户
export function deleteUser(userId) {
    return request({
        url: userUrl.deleteUser + '/' + userId,
        method: 'delete'
    });
}


export class exportUsers {
}


export function batchDeleteUsers(userIds) {
    return request({
        url: userUrl.batchDelete,
        method: 'delete',
        data: userIds
    });
}