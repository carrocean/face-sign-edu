// 导入创建好的axios实例
import { request } from '@/utils/Request.js';

// 接口配置项
var prefix = '/api/face/sign/face/';
var faceUrl = {
    getAllFaces: prefix + 'list', // 获取所有人脸数据
    getAllPageFaces: prefix + 'page-list', // 条件分页查询
    getFaceById: prefix + 'getById', // 根据ID获取人脸数据详情
    saveFace: prefix + 'save', // 添加新人脸数据
    updateFace: prefix + 'update', // 更新人脸数据
    deleteFace: prefix + 'delete', // 删除人脸数据
    batchDeleteFaces: prefix + 'delete/batch' // 批量删除人脸数据
};

// 条件分页查询
export function getAllPageFaces(params, data) {
    return request({
        url: faceUrl.getAllPageFaces,
        method: 'post',
        params: params,
        data: data
    });
}

// 获取所有人脸数据
export function getAllFaces() {
    return request({
        url: faceUrl.getAllFaces,
        method: 'get'
    });
}

// 根据ID获取人脸数据详情
export function getFaceById(faceId) {
    return request({
        url: faceUrl.getFaceById + '/' + faceId,
        method: 'get'
    });
}

// 添加新人脸数据
export function saveFace(data) {
    return request({
        url: faceUrl.saveFace,
        method: 'post',
        data: data
    });
}

// 更新人脸数据
export function updateFace(data) {
    return request({
        url: faceUrl.updateFace,
        method: 'put',
        data: data
    });
}

// 删除人脸数据
export function deleteFace(faceId) {
    return request({
        url: faceUrl.deleteFace + '/' + faceId,
        method: 'delete'
    });
}

// 批量删除人脸数据
export function batchDeleteFaces(ids) {
    return request({
        url: faceUrl.batchDeleteFaces,
        method: 'delete',
        data: ids
    });
}