package com.face.sign.recognition.service.Impl;

import com.baidu.aip.face.AipFace;
import com.face.sign.recognition.properties.BaiduFaceProperties;
import com.face.sign.recognition.service.IAttendanceService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AttendanceServiceImpl implements IAttendanceService {

    private final AipFace client;

    @Autowired
    public AttendanceServiceImpl(BaiduFaceProperties baiduFaceProperties) {
        this.client = new AipFace(baiduFaceProperties.getAppId(), baiduFaceProperties.getApiKey(), baiduFaceProperties.getSecretKey());
    }

    @Override
    public JSONObject detectFace(String imageBase64) {
        HashMap<String, Object> options = new HashMap<>();
        options.put("face_field", "age,gender,emotion");
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        return client.detect(imageBase64, "BASE64", options);
    }

    @Override
    public JSONObject registerFace(String userId, String imageBase64, String groupId) {
        HashMap<String, String> options = new HashMap<>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("action_type", "REPLACE");
        return client.addUser(imageBase64, "BASE64", groupId, userId, options);
    }

    @Override
    public JSONObject searchFace(String imageBase64, String groupId) {
        HashMap<String, Object> options = new HashMap<>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        return client.search(imageBase64, "BASE64", groupId, options);
    }

    @Override
    public JSONObject updateFace(String userId, String imageBase64, String groupId) {
        HashMap<String, String> options = new HashMap<>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        return client.updateUser(imageBase64, "BASE64", groupId, userId, options);
    }

    @Override
    public JSONObject deleteFace(String userId, String faceToken, String groupId) {
        return client.faceDelete(userId, groupId, faceToken, null);
    }

    @Override
    public JSONObject getUserInfo(String userId, String groupId) {
        return client.getUser(userId, groupId, null);
    }

    @Override
    public JSONObject getUserFaceList(String userId, String groupId) {
        return client.faceGetlist(userId, groupId, null);
    }

    @Override
    public JSONObject getUserList(String groupId) {
        return client.getGroupUsers(groupId, null);
    }


    @Override
    public JSONObject deleteUser(String userId, String groupId) {
        return client.deleteUser(userId, groupId, null);
    }

    @Override
    public JSONObject createGroup(String groupId) {
        return client.groupAdd(groupId, null);
    }

    @Override
    public JSONObject deleteGroup(String groupId) {
        return client.groupDelete(groupId, null);
    }

    @Override
    public JSONObject getGroupList() {
        return client.getGroupList(null);
    }

}