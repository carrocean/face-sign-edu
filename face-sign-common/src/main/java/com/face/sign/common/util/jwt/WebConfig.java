package com.face.sign.common.util.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtFilter jwtInterceptor;

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 拦截所有请求，除了登录和注册接口
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/**") // 拦截所有请求
//                .excludePathPatterns("/api/face/sign/user/login", "/api/face/sign/user/register"); // 排除登录和注册接口
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost") // 允许的来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 允许发送Cookie
                .maxAge(3600); // 预检请求的有效期（单位：秒）
    }
}