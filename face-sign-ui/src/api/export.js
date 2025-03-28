// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/export/';
var exportUrl = {
    exportAttendance: prefix + 'attendance', // 导出考勤记录
    exportLeave: prefix + 'leave', // 导出请假记录
    getExportHistory: prefix + 'history', // 获取导出历史
    downloadFile: prefix + 'download' // 下载导出文件
};

// 导出考勤记录
export function exportAttendance(data) {
    return request({
        url: exportUrl.exportAttendance,
        method: 'post',
        data: data,
        responseType: 'blob' // 指定响应类型为blob
    });
}

// 导出请假记录
export function exportLeave(data) {
    return request({
        url: exportUrl.exportLeave,
        method: 'post',
        data: data,
        responseType: 'blob' // 指定响应类型为blob
    });
}

// 获取导出历史
export function getExportHistory() {
    return request({
        url: exportUrl.getExportHistory,
        method: 'get'
    });
}

// 下载导出文件
export function downloadFile(fileId) {
    return request({
        url: exportUrl.downloadFile + '/' + fileId,
        method: 'get',
        responseType: 'blob' // 指定响应类型为blob
    });
} 