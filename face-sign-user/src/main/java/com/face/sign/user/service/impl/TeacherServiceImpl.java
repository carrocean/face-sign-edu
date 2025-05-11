package com.face.sign.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.SnowflakeUtils;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.user.bean.dto.ExcelTeacherDto;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.mapper.UserMapper;
import com.face.sign.user.service.ITeacherService;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl extends BaseServiceImpl<TeacherEntity, TeacherMapper> implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importTeachers(List<ExcelTeacherDto> teacherDataList) {
        for (ExcelTeacherDto teacherData : teacherDataList) {
            // 检查教师是否存在
            TeacherEntity teacherEntity = teacherMapper.selectOne(new QueryWrapper<TeacherEntity>().eq("teacher_number", teacherData.getTeacherNumber()));
            if(teacherEntity != null) {
                continue;
            }
            UserEntity user = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("account", teacherData.getTeacherNumber()));
            if (user != null) {
                continue;
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setAccount(teacherData.getTeacherNumber());
            userEntity.setPassword(SecurityUtils.encodePassword(teacherData.getTeacherNumber()));
            userEntity.setRole("teacher");
            Long userId = Long.valueOf(SnowflakeUtils.createId());
            userEntity.setId(userId);
            userEntity.setStatus(1);
            userMapper.insert(userEntity);

            // 创建教师实体
            TeacherEntity teacher = new TeacherEntity();
            teacher.setName(teacherData.getName());
            teacher.setTeacherNumber(teacherData.getTeacherNumber());
            teacher.setPhone(teacherData.getPhone());
            teacher.setEmail(teacherData.getEmail());
            teacher.setUserId(userId);
            // 保存教师信息
            teacherMapper.insert(teacher);
        }
    }

    @Override
    public List<ExcelTeacherDto> queryTeachers(Map<String, Object> conditions, String token) {
        QueryWrapper<TeacherEntity> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        queryWrapper.orderByDesc("add_time");
        List<TeacherEntity> teacherEntities = teacherMapper.selectList(queryWrapper);

        List<ExcelTeacherDto> TeacherDtos = BeanUtil.copyToList(teacherEntities, ExcelTeacherDto.class);

        return TeacherDtos;
    }
}