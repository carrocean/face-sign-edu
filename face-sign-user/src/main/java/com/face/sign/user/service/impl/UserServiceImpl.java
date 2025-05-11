package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.system.entity.SystemLogEntity;
import com.face.sign.system.mapper.SystemLogMapper;
import com.face.sign.user.bean.dto.ExcelLogDto;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.AdministratorMapper;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.mapper.UserMapper;
import com.face.sign.user.qo.UserQo;
import com.face.sign.user.service.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserMapper> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Autowired
    private AdministratorMapper administratorMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public UserEntity login(UserEntity theUser) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", theUser.getAccount());
        UserEntity user = userMapper.selectOne(queryWrapper);
        if (user != null && SecurityUtils.matchesPassword(theUser.getPassword(), user.getPassword())) {
            // 更新用户最后登录时间、IP和登录次数
            UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", user.getId())
                    .set("last_login_time", new Date())
                    .set("last_login_ip", IpUtils.getCurrentClientIp())
                    .setSql("login_count = login_count + 1");
            userMapper.update(null, updateWrapper);
            return user;
        }
        return null;
    }

    @Override
    public int register(UserEntity user) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", user.getAccount());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BizException("账号已存在");
        }
        user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
        return userMapper.insert(user);
    }

    @Override
    public void resetPassword(UserQo user, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        UserEntity userEntity = userMapper.selectById(userId);
        if(!SecurityUtils.matchesPassword(user.getOldPassword(), userEntity.getPassword())) {
            throw new BizException("原密码错误");
        }
        if(!user.getNewPassword().equals(user.getOldPassword())) {
            throw new BizException("新密码不一致");
        }

        userEntity.setUpdateTime(new Date());
        userEntity.setPassword(SecurityUtils.encodePassword(user.getNewPassword()));
        userMapper.updateById(userEntity);
    }

    @Override
    public List<ExcelLogDto> exportLogs() {
        QueryWrapper<SystemLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("add_time");
        List<SystemLogEntity> logEntities = systemLogMapper.selectList(queryWrapper);

        List<ExcelLogDto> excelLogDtos = new ArrayList<>();
        for (SystemLogEntity logEntity : logEntities) {
            ExcelLogDto excelLogDto = new ExcelLogDto();

            UserEntity userEntity = userMapper.selectById(logEntity.getUserId());
            if (userEntity == null) {
                continue;
            }

            excelLogDto.setOperation(logEntity.getOperation());
            excelLogDto.setOperationTime(logEntity.getOperationTime());
            excelLogDto.setIpAddress(logEntity.getIpAddress());

            if ("admin".equals(userEntity.getRole())) {
                AdministratorEntity administratorEntity = administratorMapper.selectOne(new QueryWrapper<AdministratorEntity>().eq("user_id", userEntity.getId()));
                if (administratorEntity != null) {
                    excelLogDto.setName(administratorEntity.getName());
                } else {
                    excelLogDto.setName("未知管理员");
                }
            } else if ("teacher".equals(userEntity.getRole())) {
                TeacherEntity teacherEntity = teacherMapper.selectOne(new QueryWrapper<TeacherEntity>().eq("user_id", userEntity.getId()));
                if (teacherEntity != null) {
                    excelLogDto.setName(teacherEntity.getName());
                } else {
                    excelLogDto.setName("未知教师");
                }
            } else if ("student".equals(userEntity.getRole())) {
                StudentEntity studentEntity = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userEntity.getId()));
                if (studentEntity != null) {
                    excelLogDto.setName(studentEntity.getName());
                } else {
                    excelLogDto.setName("未知学生");
                }
            } else {
                excelLogDto.setName("未知角色");
            }

            excelLogDtos.add(excelLogDto);
        }

        return excelLogDtos;
    }


}