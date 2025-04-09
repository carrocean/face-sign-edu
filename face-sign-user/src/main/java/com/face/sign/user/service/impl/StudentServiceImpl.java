package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.service.IStudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends BaseServiceImpl<StudentEntity, StudentMapper> implements IStudentService {


    @Override
    public StudentEntity getByUserId(Long id) {
        return getOne(Wrappers.<StudentEntity>lambdaQuery().eq(StudentEntity::getUserId, id));
    }
}