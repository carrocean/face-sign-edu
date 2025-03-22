package com.face.sign.recognition.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "baidu.face")
public class BaiduFaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
}