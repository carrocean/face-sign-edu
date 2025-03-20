package com.face.sign.recognition.config;

import com.face.sign.common.util.RedisUtil;
import com.face.sign.recognition.properties.BaiduFaceProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.Map;

/**
 * 获取百度人脸识别accessToken
 */
@Service
public class BaiduFaceConfig {
    @Autowired
    private BaiduFaceProperties baiduFaceProperties;
    @Autowired
    private RedisUtil redisUtil;

    public static final String ACCESS_TOKEN_KEY = "";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 7180; // 7180秒，小于30天

    public synchronized String getAccessToken() throws IOException {
        String accessToken = redisUtil.getCache(ACCESS_TOKEN_KEY);
        if (accessToken == null || accessToken.isEmpty()) {
            OkHttpClient client = new OkHttpClient();
            HttpUrl url = HttpUrl.parse(baiduFaceProperties.getTokenUrl()).newBuilder()
                    .addQueryParameter("grant_type", "client_credentials")
                    .addQueryParameter("client_id", baiduFaceProperties.getApiKey())
                    .addQueryParameter("client_secret", baiduFaceProperties.getSecretKey())
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Map<String, Object> result = new ObjectMapper().readValue(response.body().string(), Map.class);
            accessToken = (String) result.get("access_token");
            redisUtil.setCache(ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        return accessToken;
    }
}