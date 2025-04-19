package com.face.sign.recognition.util;

import com.baidu.aip.face.AipFace;
import com.face.sign.recognition.properties.BaiduFaceProperties;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度人脸识别API工具类
 * 提供人脸检测、注册、搜索、更新、删除等功能的封装
 */
@Component
public class BaiduFaceUtil {

    private final AipFace client;
    private final Map<String, String> defaultOptions;

    public BaiduFaceUtil(BaiduFaceProperties baiduFaceProperties) {
        this.client = new AipFace(
            baiduFaceProperties.getAppId(),
            baiduFaceProperties.getApiKey(),
            baiduFaceProperties.getSecretKey()
        );

        this.defaultOptions = new HashMap<>();
        // 图片质量控制：NONE-不控制, LOW-较低要求, NORMAL-一般要求, HIGH-较高要求
        defaultOptions.put("quality_control", "LOW");
        // 活体检测控制：NONE-不控制, LOW-低要求(高通过率), NORMAL-一般要求, HIGH-高要求(高拒绝率)
        defaultOptions.put("liveness_control", "LOW");
    }

    /**
     * 人脸检测：判断上传的图片中是否具有面部信息
     * @param imageBase64 图片的base64编码
     * @return 检测结果，包含年龄、性别、情绪等信息
     */
    public JSONObject detectFace(String imageBase64) {
        HashMap<String, Object> options = new HashMap<>(defaultOptions);
        options.put("face_field", "age,gender,emotion");
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");
        return client.detect(imageBase64, "BASE64", options);
    }

    /**
     * 人脸注册
     * @param userId 用户ID
     * @param imageBase64 图片的base64编码
     * @param groupId 用户组ID
     * @return 注册结果
     */
    public JSONObject registerFace(String userId, String imageBase64, String groupId) {
        HashMap<String, String> options = new HashMap<>(defaultOptions);
        options.put("user_info", "user's info");
        options.put("action_type", "REPLACE");
        return client.addUser(imageBase64, "BASE64", groupId, userId, options);
    }

    /**
     * 人脸搜索
     * @param imageBase64 图片的base64编码
     * @param groupId 用户组ID
     * @return 搜索结果，包含匹配的人脸信息
     */
    public JSONObject searchFace(String imageBase64, String groupId) {
        HashMap<String, Object> options = new HashMap<>(defaultOptions);
        return client.search(imageBase64, "BASE64", groupId, options);
    }

    /**
     * 更新人脸信息
     * @param userId 用户ID
     * @param imageBase64 新的图片base64编码
     * @param groupId 用户组ID
     * @return 更新结果
     */
    public JSONObject updateFace(String userId, String imageBase64, String groupId) {
        HashMap<String, String> options = new HashMap<>(defaultOptions);
        options.put("user_info", "user's info");
        return client.updateUser(imageBase64, "BASE64", groupId, userId, options);
    }

    /**
     * 删除人脸
     * @param userId 用户ID
     * @param faceToken 人脸标识
     * @param groupId 用户组ID
     * @return 删除结果
     */
    public JSONObject deleteFace(String userId, String faceToken, String groupId) {
        return client.faceDelete(userId, groupId, faceToken, null);
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @param groupId 用户组ID
     * @return 用户信息
     */
    public JSONObject getUserInfo(String userId, String groupId) {
        return client.getUser(userId, groupId, null);
    }

    /**
     * 获取用户人脸列表
     * @param userId 用户ID
     * @param groupId 用户组ID
     * @return 用户人脸列表
     */
    public JSONObject getUserFaceList(String userId, String groupId) {
        return client.faceGetlist(userId, groupId, null);
    }

    /**
     * 获取用户组下的用户列表
     * @param groupId 用户组ID
     * @return 用户列表
     */
    public JSONObject getUserList(String groupId) {
        return client.getGroupUsers(groupId, null);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @param groupId 用户组ID
     * @return 删除结果
     */
    public JSONObject deleteUser(String userId, String groupId) {
        return client.deleteUser(userId, groupId, null);
    }

    /**
     * 创建用户组
     * @param groupId 用户组ID
     * @return 创建结果
     */
    public JSONObject createGroup(String groupId) {
        return client.groupAdd(groupId, null);
    }

    /**
     * 删除用户组
     * @param groupId 用户组ID
     * @return 删除结果
     */
    public JSONObject deleteGroup(String groupId) {
        return client.groupDelete(groupId, null);
    }

    /**
     * 获取用户组列表
     * @return 用户组列表
     */
    public JSONObject getGroupList() {
        return client.getGroupList(null);
    }
}
