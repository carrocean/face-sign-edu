import { request } from '@/utils/Request.js';

// 获取当前考勤信息
export function getCurrentAttendance() {
  return request({
    url: '/attendance/current',
    method: 'get'
  })
}

// 获取考勤记录列表
export function getAttendanceList(params) {
  return request({
    url: '/attendance/list',
    method: 'get',
    params
  })
}

// 人脸识别考勤
export function checkIn(data) {
  return request({
    url: '/attendance/check-in',
    method: 'post',
    data
  })
}

// 获取考勤详情
export function getAttendanceDetail(attendanceId) {
  return request({
    url: `/attendance/detail/${attendanceId}`,
    method: 'get'
  })
}

// 导出考勤记录
export function exportAttendance(attendanceId) {
  return request({
    url: `/attendance/export/${attendanceId}`,
    method: 'get',
    responseType: 'blob'
  })
} 