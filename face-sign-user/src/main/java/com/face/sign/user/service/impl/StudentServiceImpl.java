package com.face.sign.user.service.impl;

import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends BaseServiceImpl<StudentEntity, StudentMapper> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public boolean save(StudentEntity student) {
        // 检查学号是否已存在
        if (studentMapper.checkStudentNumberExists(student.getStudentNumber()) > 0) {
            throw new RuntimeException("学号已存在");
        }
        return super.save(student);
    }

    @Override
    public boolean updateById(StudentEntity student) {
        // 如果修改了学号，需要检查新学号是否已存在
        if (student.getStudentNumber() != null) {
            StudentEntity oldStudent = studentMapper.selectById(student.getId());
            if (oldStudent != null && !oldStudent.getStudentNumber().equals(student.getStudentNumber())) {
                if (studentMapper.checkStudentNumberExists(student.getStudentNumber()) > 0) {
                    throw new RuntimeException("学号已存在");
                }
            }
        }
        return super.updateById(student);
    }
}