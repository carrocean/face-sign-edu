package com.face.sign.recognition.service;

import com.face.sign.recognition.config.BaiduFaceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaceAttendanceService {
    @Autowired
    private BaiduFaceConfig baiduFaceConfig;

    @Value("${baidu.face.searchUrl}")
    private String searchUrl;

    /**
     * 人脸搜索
     * @param imageBase64
     * @param groupIdList
     * @return
     * @throws IOException
     */
    public Map<String, Object> searchFace(String imageBase64, String groupIdList) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", imageBase64);
        requestBody.put("image_type", "BASE64");
        requestBody.put("group_id_list", groupIdList);
        requestBody.put("quality_control", "LOW");
        requestBody.put("liveness_control", "NORMAL");

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url(searchUrl + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 人脸注册
     * @param imageBase64
     * @param groupId
     * @param userId
     * @param userInfo
     * @return
     * @throws IOException
     */
    public Map<String, Object> addFace(String imageBase64, String groupId, String userId, String userInfo) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", imageBase64);
        requestBody.put("image_type", "BASE64");
        requestBody.put("group_id", groupId);
        requestBody.put("user_id", userId);
        requestBody.put("user_info", userInfo);
        requestBody.put("quality_control", "LOW");
        requestBody.put("liveness_control", "NORMAL");

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 删除人脸
     * @param groupId 用户组ID
     * @param userId 用户ID
     * @param faceToken 人脸token
     * @return 删除结果
     */
    public Map<String, Object> deleteFace(String groupId, String userId, String faceToken) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("group_id", groupId);
        requestBody.put("user_id", userId);
        requestBody.put("face_token", faceToken);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/delete" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 更新人脸信息
     * @param imageBase64 图片base64
     * @param groupId 用户组ID
     * @param userId 用户ID
     * @param userInfo 用户信息
     * @return 更新结果
     */
    public Map<String, Object> updateFace(String imageBase64, String groupId, String userId, String userInfo) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image", imageBase64);
        requestBody.put("image_type", "BASE64");
        requestBody.put("group_id", groupId);
        requestBody.put("user_id", userId);
        requestBody.put("user_info", userInfo);
        requestBody.put("quality_control", "LOW");
        requestBody.put("liveness_control", "NORMAL");

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 创建人脸库
     * @param groupId 用户组ID
     * @param groupName 用户组名称
     * @return 创建结果
     */
    public Map<String, Object> createGroup(String groupId, String groupName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("group_id", groupId);
        requestBody.put("group_name", groupName);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/add" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 删除人脸库
     * @param groupId 用户组ID
     * @return 删除结果
     */
    public Map<String, Object> deleteGroup(String groupId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("group_id", groupId);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/delete" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 批量注册人脸
     * @param faceList 人脸信息列表，每个元素包含imageBase64、userId、userInfo
     * @param groupId 用户组ID
     * @return 批量注册结果
     */
    public Map<String, Object> batchAddFaces(List<Map<String, String>> faceList, String groupId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("group_id", groupId);
        requestBody.put("user_list", faceList.stream().map(face -> {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("image", face.get("imageBase64"));
            userInfo.put("image_type", "BASE64");
            userInfo.put("user_id", face.get("userId"));
            userInfo.put("user_info", face.get("userInfo"));
            return userInfo;
        }).toList());

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 获取用户组列表
     * @return 用户组列表
     */
    public Map<String, Object> getGroupList() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getlist" + "?access_token=" + accessToken)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 获取用户组中的用户列表
     * @param groupId 用户组ID
     * @return 用户列表
     */
    public Map<String, Object> getUserList(String groupId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("group_id", groupId);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }

    /**
     * 获取用户的人脸列表
     * @param userId 用户ID
     * @param groupId 用户组ID
     * @return 人脸列表
     */
    public Map<String, Object> getFaceList(String userId, String groupId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String accessToken = baiduFaceConfig.getAccessToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("group_id", groupId);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new ObjectMapper().writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/getlist" + "?access_token=" + accessToken)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return new ObjectMapper().readValue(response.body().string(), Map.class);
    }
}