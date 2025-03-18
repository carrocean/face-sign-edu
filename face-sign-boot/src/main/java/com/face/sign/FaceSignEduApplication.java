package com.face.sign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan({"com.face.sign.user.mapper"})
public class FaceSignEduApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceSignEduApplication.class, args);
    }

}
