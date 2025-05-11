//导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/user/';
var userUrl = {
    login: prefix + 'login',
    logout: prefix + 'logout',
    register: prefix + 'register',
    checkUserLoginInfo: prefix + 'checkUserLoginInfo',
    getAllUsers: prefix + 'list', // 获取所有用户
    getAllPageUsers: prefix + 'page-list', // 条件分页查询
    getUserById: prefix + 'getById', // 根据ID获取用户详情
    saveUser: prefix + 'save', // 添加新用户
    updateUser: prefix + 'update', // 更新用户
    deleteUser: prefix + 'delete', // 删除用户
    batchDelete: prefix + 'delete/batch', // 批量删除用户
    resetPassword: prefix + 'resetPassword', // 修改密码
    exportLogs: prefix + 'export-log', // 导出日志
}

export function login(data) {
    return request({
        url: userUrl.login,
        method: 'post',
        data:data,
    })
}

export function logout(data) {
    return request({
        url: userUrl.logout,
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
export function getAllPageUsers(params, data) {
    return request({
        url: userUrl.getAllPageUsers,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有学生
export function getAllUsers() {
    return request({
        url: userUrl.getAllUsers,
        method: 'get'
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
export function resetPassword(data) {
    return request({
        url: userUrl.resetPassword,
        method: 'post',
        data: data
    });
}

// 更新密码
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

// 批量删除用户
export function batchDeleteUsers(userIds) {
    return request({
        url: userUrl.batchDelete,
        method: 'delete',
        data: userIds
    });
}

export function exportLogs(data) {
    return request({
        url: userUrl.exportLogs,
        method: 'post',
        data: data,
        responseType: 'blob'
    });
}