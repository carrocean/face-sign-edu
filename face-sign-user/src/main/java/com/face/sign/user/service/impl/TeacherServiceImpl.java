package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.service.ITeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends BaseServiceImpl<TeacherEntity, TeacherMapper> implements ITeacherService {

    @Override
    public TeacherEntity getByUserId(Long id) {
        return getOne(Wrappers.<TeacherEntity>lambdaQuery().eq(TeacherEntity::getUserId, id));
    }

}