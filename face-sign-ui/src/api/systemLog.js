// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/system-log/';
var systemLogUrl = {
    getAllSystemLogs: prefix + 'list', // 获取所有系统日志
    getAllPageSystemLogs: prefix + 'page-list', // 条件分页查询
    getSystemLogById: prefix + 'getById', // 根据ID获取系统日志详情
    saveSystemLog: prefix + 'save', // 添加新系统日志
    updateSystemLog: prefix + 'update', // 更新系统日志
    deleteSystemLog: prefix + 'delete', // 删除系统日志
    batchDeleteSystemLogs: prefix + 'delete/batch' // 批量删除系统日志
};

// 条件分页查询
export function getAllPageSystemLogs(params, data) {
    return request({
        url: systemLogUrl.getAllPageSystemLogs,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有系统日志
export function getAllSystemLogs() {
    return request({
        url: systemLogUrl.getAllSystemLogs,
        method: 'get'
    });
}

// 根据ID获取系统日志详情
export function getSystemLogById(systemLogId) {
    return request({
        url: systemLogUrl.getSystemLogById + '/' + systemLogId,
        method: 'get'
    });
}

// 添加新系统日志
export function saveSystemLog(data) {
    return request({
        url: systemLogUrl.saveSystemLog,
        method: 'post',
        data: data
    });
}

// 更新系统日志
export function updateSystemLog(data) {
    return request({
        url: systemLogUrl.updateSystemLog,
        method: 'put',
        data: data
    });
}

// 删除系统日志
export function deleteSystemLog(systemLogId) {
    return request({
        url: systemLogUrl.deleteSystemLog + '/' + systemLogId,
        method: 'delete'
    });
}

// 批量删除系统日志
export function batchDeleteSystemLogs(ids) {
    return request({
        url: systemLogUrl.batchDeleteSystemLogs,
        method: 'delete',
        data: ids
    });
}

export class exportLogs {
}