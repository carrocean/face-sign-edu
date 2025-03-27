//导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/user/';
var managerUrl = {
    login: prefix + 'login',
    register: prefix + 'register',
    checkUserLoginInfo: prefix + 'checkUserLoginInfo',
    getAllUsers: prefix + '/list', // 获取所有用户
    getUserById: prefix + '/getById', // 根据ID获取用户详情
    saveUser: prefix + '/save', // 添加新用户
    updateUser: prefix + '/update', // 更新用户
    deleteUser: prefix + '/delete' // 删除用户
}

export function login(data) {
    return request({
        url: managerUrl.login,
        method: 'post',
        data:data,
    })
}

export function register(data) {
    return request({
        url: managerUrl.register,
        method: 'post',
        data:data,
    })
}

export function checkUserLoginInfo() {
    return request({
        url: managerUrl.checkUserLoginInfo,
        method: 'get',
    })
}

// 获取所有用户
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
