package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/student")
public class StudentController extends BaseController<StudentEntity, IStudentService> {

    IStudentService studentService;

    @Autowired
    public void setStudentService(IStudentService studentService) {
        this.studentService = studentService;
        init(studentService);
    }
}