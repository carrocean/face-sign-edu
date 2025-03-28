package com.face.sign.user.service.impl;

import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherServiceImpl extends BaseServiceImpl<TeacherEntity, TeacherMapper> implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TeacherEntity teacher) {
        // 检查工号是否已存在
        if (teacherMapper.checkTeacherNumberExists(teacher.getTeacherNumber()) > 0) {
            throw new RuntimeException("工号已存在");
        }
        return super.save(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(TeacherEntity teacher) {
        // 如果修改了工号，需要检查新工号是否已存在
        if (teacher.getTeacherNumber() != null) {
            TeacherEntity oldTeacher = teacherMapper.selectById(teacher.getId());
            if (oldTeacher != null && !oldTeacher.getTeacherNumber().equals(teacher.getTeacherNumber())) {
                if (teacherMapper.checkTeacherNumberExists(teacher.getTeacherNumber()) > 0) {
                    throw new RuntimeException("工号已存在");
                }
            }
        }
        return super.updateById(teacher);
    }
}