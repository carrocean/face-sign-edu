// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/notification/';
var notificationUrl = {
    getAllNotifications: prefix + 'list', // 获取所有通知
    getAllPageNotifications: prefix + 'page-list', // 条件分页查询
    getNotificationById: prefix + 'getById', // 根据ID获取通知详情
    saveNotification: prefix + 'save', // 添加新通知
    updateNotification: prefix + 'update', // 更新通知
    deleteNotification: prefix + 'delete', // 删除通知
    batchDeleteNotifications: prefix + 'delete/batch' // 批量删除通知
};

// 条件分页查询
export function getAllPageNotifications(params, data) {
    return request({
        url: notificationUrl.getAllPageNotifications,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有通知
export function getAllNotifications() {
    return request({
        url: notificationUrl.getAllNotifications,
        method: 'get'
    });
}

// 根据ID获取通知详情
export function getNotificationById(notificationId) {
    return request({
        url: notificationUrl.getNotificationById + '/' + notificationId,
        method: 'get'
    });
}

// 添加新通知
export function saveNotification(data) {
    return request({
        url: notificationUrl.saveNotification,
        method: 'post',
        data: data
    });
}

// 更新通知
export function updateNotification(data) {
    return request({
        url: notificationUrl.updateNotification,
        method: 'put',
        data: data
    });
}

// 删除通知
export function deleteNotification(notificationId) {
    return request({
        url: notificationUrl.deleteNotification + '/' + notificationId,
        method: 'delete'
    });
}

// 批量删除通知
export function batchDeleteNotifications(ids) {
    return request({
        url: notificationUrl.batchDeleteNotifications,
        method: 'delete',
        data: ids
    });
}