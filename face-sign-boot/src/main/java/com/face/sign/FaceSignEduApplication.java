package com.face.sign;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.face.sign.user.mapper", "com.face.sign.course.mapper", "com.face.sign.attendance.mapper", "com.face.sign.notification.mapper", "com.face.sign.system.mapper"})
public class FaceSignEduApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceSignEduApplication.class, args);
    }

}
