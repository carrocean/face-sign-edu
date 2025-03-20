package com.face.sign.recognition.timer;

import com.face.sign.common.util.RedisUtil;
import com.face.sign.recognition.config.BaiduFaceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 定时刷新accessToken
 */
@Component
public class AccessTokenScheduler {
    @Autowired
    private BaiduFaceConfig baiduFaceConfig;
    @Autowired
    private RedisUtil redisUtil;

    @Scheduled(fixedDelay = 7180000) // 7180秒后再次执行
    public void refreshAccessToken() {
        try {
            String newAccessToken = baiduFaceConfig.getAccessToken();
            redisUtil.setCache(BaiduFaceConfig.ACCESS_TOKEN_KEY, newAccessToken, BaiduFaceConfig.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}