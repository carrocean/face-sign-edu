package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.service.IStudentService;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends BaseServiceImpl<StudentEntity, StudentMapper> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private IUserService userService;

    @Override
    public StudentEntity getByUserId(Long id) {
        return getOne(Wrappers.<StudentEntity>lambdaQuery().eq(StudentEntity::getUserId, id));
    }

    @Override
    public void addStudent(StudentEntity student) {
        if(student.getStudentNumber().isEmpty()) {
            throw new BizException("学号不可为空");
        }

        UserEntity user = new UserEntity();
        user.setAccount(student.getStudentNumber());
        user.setPassword(student.getStudentNumber());
        user.setRole("student");
        user.setStatus(1);
        userService.register(user);

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", user.getAccount());
        UserEntity registeredUser = userService.getOne(queryWrapper);
        
        if(registeredUser != null) {
            student.setUserId(registeredUser.getId());
            studentMapper.insert(student);
        } else {
            throw new BizException("用户注册失败");
        }
    }
}