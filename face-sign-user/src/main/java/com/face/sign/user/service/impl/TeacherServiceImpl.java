package com.face.sign.user.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends IBaseServiceImpl<TeacherEntity, TeacherMapper> implements ITeacherService {

    private TeacherMapper teacherMapper;

    @Autowired
    public void setTeacherMapper(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
        init(teacherMapper);
    }
}