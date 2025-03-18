package com.face.sign.user.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends IBaseServiceImpl<StudentEntity, StudentMapper> implements IStudentService {

    private StudentMapper studentMapper;

    @Autowired
    public void setStudentMapper(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
        init(studentMapper);
    }
}