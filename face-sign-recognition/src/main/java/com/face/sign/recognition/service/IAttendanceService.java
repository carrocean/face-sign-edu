package com.face.sign.recognition.service;

import org.json.JSONObject;

public interface IAttendanceService {

    /**
     * 人脸检测
     *
     * @param imageBase64 图片的Base64编码
     * @return 检测结果的JSON对象
     */
    JSONObject detectFace(String imageBase64);

    /**
     * 人脸注册
     *
     * @param userId      用户ID
     * @param imageBase64 图片的Base64编码
     * @param groupId     用户组ID
     * @return 注册结果的JSON对象
     */
    JSONObject registerFace(String userId, String imageBase64, String groupId);

    /**
     * 人脸搜索（1:N识别）
     *
     * @param imageBase64 图片的Base64编码
     * @param groupId     用户组ID
     * @return 搜索结果的JSON对象
     */
    JSONObject searchFace(String imageBase64, String groupId);

    /**
     * 人脸更新
     *
     * @param userId      用户ID
     * @param imageBase64 图片的Base64编码
     * @param groupId     用户组ID
     * @return 更新结果的JSON对象
     */
    JSONObject updateFace(String userId, String imageBase64, String groupId);

    /**
     * 删除人脸
     *
     * @param userId      用户ID
     * @param faceToken   人脸Token
     * @param groupId     用户组ID
     * @return 删除结果的JSON对象
     */
    JSONObject deleteFace(String userId, String faceToken, String groupId);

    /**
     * 获取用户信息
     *
     * @param userId  用户ID
     * @param groupId 用户组ID
     * @return 用户信息的JSON对象
     */
    JSONObject getUserInfo(String userId, String groupId);

    /**
     * 获取用户人脸列表
     *
     * @param userId  用户ID
     * @param groupId 用户组ID
     * @return 用户人脸列表的JSON对象
     */
    JSONObject getUserFaceList(String userId, String groupId);

    /**
     * 获取用户列表
     *
     * @param groupId 用户组ID
     * @return 用户列表的JSON对象
     */
    JSONObject getUserList(String groupId);

    /**
     * 删除用户
     *
     * @param userId  用户ID
     * @param groupId 用户组ID
     * @return 删除结果的JSON对象
     */
    JSONObject deleteUser(String userId, String groupId);

    /**
     * 创建用户组
     *
     * @param groupId 用户组ID
     * @return 创建结果的JSON对象
     */
    JSONObject createGroup(String groupId);

    /**
     * 删除用户组
     *
     * @param groupId 用户组ID
     * @return 删除结果的JSON对象
     */
    JSONObject deleteGroup(String groupId);

    /**
     * 获取用户组列表
     *
     * @return 用户组列表的JSON对象
     */
    JSONObject getGroupList();

}