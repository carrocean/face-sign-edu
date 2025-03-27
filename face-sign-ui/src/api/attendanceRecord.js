// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/attendance-record';
var attendanceRecordUrl = {
    getAllAttendanceRecords: prefix + '/list', // 获取所有考勤记录
    getAttendanceRecordById: prefix + '/getById', // 根据ID获取考勤记录详情
    saveAttendanceRecord: prefix + '/save', // 添加新考勤记录
    updateAttendanceRecord: prefix + '/update', // 更新考勤记录
    deleteAttendanceRecord: prefix + '/delete', // 删除考勤记录
    signIn: prefix + '/sign-in' // 签到
};

// 获取所有考勤记录
export function getAllAttendanceRecords() {
    return request({
        url: attendanceRecordUrl.getAllAttendanceRecords,
        method: 'get'
    });
}

// 根据ID获取考勤记录详情
export function getAttendanceRecordById(attendanceRecordId) {
    return request({
        url: attendanceRecordUrl.getAttendanceRecordById + '/' + attendanceRecordId,
        method: 'get'
    });
}

// 添加新考勤记录
export function saveAttendanceRecord(data) {
    return request({
        url: attendanceRecordUrl.saveAttendanceRecord,
        method: 'post',
        data: data
    });
}

// 更新考勤记录
export function updateAttendanceRecord(data) {
    return request({
        url: attendanceRecordUrl.updateAttendanceRecord,
        method: 'put',
        data: data
    });
}

// 删除考勤记录
export function deleteAttendanceRecord(attendanceRecordId) {
    return request({
        url: attendanceRecordUrl.deleteAttendanceRecord + '/' + attendanceRecordId,
        method: 'delete'
    });
}

// 获取考勤记录列表
export function getAttendanceList(params) {
    return request({
        url: '/attendance/list',
        method: 'get',
        params
    });
}

// 签到
export function signIn(data) {
    return request({
        url: attendanceRecordUrl.signIn,
        method: 'post',
        data: data
    });
}
