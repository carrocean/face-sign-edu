// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/leave-request/';
var leaveRequestUrl = {
    getAllLeaveRequests: prefix + 'list', // 获取所有请假记录
    getLeaveRequestById: prefix + 'getById', // 根据ID获取请假记录详情
    saveLeaveRequest: prefix + 'save', // 添加新请假记录
    updateLeaveRequest: prefix + 'update', // 更新请假记录
    deleteLeaveRequest: prefix + 'delete' // 删除请假记录
};

// 获取所有请假记录
export function getAllLeaveRequests() {
    return request({
        url: leaveRequestUrl.getAllLeaveRequests,
        method: 'get'
    });
}

// 根据ID获取请假记录详情
export function getLeaveRequestById(leaveRequestId) {
    return request({
        url: leaveRequestUrl.getLeaveRequestById + '/' + leaveRequestId,
        method: 'get'
    });
}

// 添加新请假记录
export function saveLeaveRequest(data) {
    return request({
        url: leaveRequestUrl.saveLeaveRequest,
        method: 'post',
        data: data
    });
}

// 更新请假记录
export function updateLeaveRequest(data) {
    return request({
        url: leaveRequestUrl.updateLeaveRequest,
        method: 'put',
        data: data
    });
}

// 删除请假记录
export function deleteLeaveRequest(leaveRequestId) {
    return request({
        url: leaveRequestUrl.deleteLeaveRequest + '/' + leaveRequestId,
        method: 'delete'
    });
}