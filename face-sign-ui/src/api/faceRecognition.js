import { request } from '@/utils/Request.js';

// 人脸检测
export function detectFace(imageBase64) {
  return request({
    url: '/recognition/detect',
    method: 'post',
    data: {
      imageBase64
    }
  })
}

// 人脸注册
export function registerFace(studentId, imageBase64, groupId) {
  return request({
    url: '/recognition/register',
    method: 'post',
    data: {
      studentId,
      imageBase64,
      groupId
    }
  })
}

// 人脸搜索
export function searchFace(imageBase64, groupId) {
  return request({
    url: '/recognition/search',
    method: 'post',
    data: {
      imageBase64,
      groupId
    }
  })
}

// 人脸更新
export function updateFace(studentId, imageBase64, groupId) {
  return request({
    url: '/recognition/update',
    method: 'post',
    data: {
      studentId,
      imageBase64,
      groupId
    }
  })
}

// 人脸删除
export function deleteFace(studentId, faceToken, groupId) {
  return request({
    url: '/recognition/delete',
    method: 'post',
    data: {
      studentId,
      faceToken,
      groupId
    }
  })
}

// 添加人脸数据
export function addFaceData(image, studentId) {
  const formData = new FormData()
  formData.append('image', image)
  formData.append('studentId', studentId)
  return request({
    url: '/recognition/face-data',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新人脸数据
export function updateFaceData(image, studentId) {
  const formData = new FormData()
  formData.append('image', image)
  formData.append('studentId', studentId)
  return request({
    url: '/recognition/face-data',
    method: 'put',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除人脸数据
export function deleteFaceData(faceId) {
  return request({
    url: `/recognition/face-data/${faceId}`,
    method: 'delete'
  })
}

// 验证人脸数据
export function verifyFaceData(image, studentId) {
  const formData = new FormData()
  formData.append('image', image)
  formData.append('studentId', studentId)
  return request({
    url: '/recognition/verify',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取识别统计
export function getRecognitionStatistics(studentId) {
  return request({
    url: `/recognition/statistics/${studentId}`,
    method: 'get'
  })
} 