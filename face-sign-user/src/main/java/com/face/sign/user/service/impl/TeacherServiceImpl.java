package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.service.ITeacherService;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends BaseServiceImpl<TeacherEntity, TeacherMapper> implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private IUserService userService;

    @Override
    public TeacherEntity getByUserId(Long id) {
        return getOne(Wrappers.<TeacherEntity>lambdaQuery().eq(TeacherEntity::getUserId, id));
    }

    @Override
    public void addTeacher(TeacherEntity teacher) {
        if(teacher.getTeacherNumber().isEmpty()) {
            throw new BizException("工号不可为空");
        }

        UserEntity user = new UserEntity();
        user.setAccount(teacher.getTeacherNumber());
        user.setPassword(teacher.getTeacherNumber());
        user.setRole("teacher");
        user.setStatus(1);
        userService.register(user);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", user.getAccount());
        UserEntity registeredUser = userService.getOne(queryWrapper);
        
        if(registeredUser != null) {
            teacher.setUserId(registeredUser.getId());
            teacherMapper.insert(teacher);
        } else {
            throw new BizException("用户注册失败");
        }
    }
}