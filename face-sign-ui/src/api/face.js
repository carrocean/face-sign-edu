import { request } from '@/utils/Request.js';

// 人脸检测
export function detectFace(imageBase64) {
  return request({
    url: '/recognition/face/detect',
    method: 'post',
    data: { imageBase64 }
  })
}

// 人脸注册
export function registerFace(studentId, imageBase64, groupId) {
  return request({
    url: '/recognition/face/register',
    method: 'post',
    data: { studentId, imageBase64, groupId }
  })
}

// 人脸搜索
export function searchFace(imageBase64, groupId) {
  return request({
    url: '/recognition/face/search',
    method: 'post',
    data: { imageBase64, groupId }
  })
}

// 人脸更新
export function updateFace(studentId, imageBase64, groupId) {
  return request({
    url: '/recognition/face/update',
    method: 'post',
    data: { studentId, imageBase64, groupId }
  })
}

// 删除人脸
export function deleteFace(studentId, faceToken, groupId) {
  return request({
    url: '/recognition/face/delete',
    method: 'post',
    data: { studentId, faceToken, groupId }
  })
}

// 获取人脸数据
export function getFaceData(studentId) {
  return request({
    url: `/recognition/face/data/${studentId}`,
    method: 'get'
  })
}

// 验证人脸
export function verifyFace(studentId, imageBase64) {
  return request({
    url: '/recognition/face/verify',
    method: 'post',
    data: { studentId, imageBase64 }
  })
}

// 获取识别日志
export function getRecognitionLogs(studentId) {
  return request({
    url: `/recognition/logs/${studentId}`,
    method: 'get'
  })
}

// 获取识别统计
export function getRecognitionStats(studentId) {
  return request({
    url: `/recognition/stats/${studentId}`,
    method: 'get'
  })
} 