package com.face.sign.common.util.minio;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: minio文件服务配置类
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.minio")
public class MinioConfig {
    //地址
    private String endpoint;
    //账号
    private String accessKey;
    //密码
    private String secretKey;

    /**
     * 初始化minio连接池
     * @return MinioClient
     */
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}