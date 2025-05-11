package com.face.sign.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.notification.entity.NotificationEntity;
import com.face.sign.notification.mapper.NotificationMapper;
import com.face.sign.notification.service.INotificationService;
import com.face.sign.user.mapper.StudentMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationServiceImpl extends BaseServiceImpl<NotificationEntity, NotificationMapper> implements INotificationService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        QueryWrapper<NotificationEntity> queryWrapper = new QueryWrapper<>();
        if(fuzzySearch){
            QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        } else {
            QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
        }
        queryWrapper.eq("receiver_id", userId);
        queryWrapper.orderByDesc("add_time");

        if(pageSearch){
            return mapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        } else {
            Page<NotificationEntity> page = new Page<>(1, Integer.MAX_VALUE);
            return mapper.selectPage(page, queryWrapper);
        }
    }

    @Override
    public Object pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        QueryWrapper<NotificationEntity> queryWrapper = new QueryWrapper<>();
        if(fuzzySearch){
            QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        } else {
            QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
        }
        queryWrapper.eq("sender_id", userId);
        queryWrapper.orderByDesc("add_time");

        if(pageSearch){
            return mapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        } else {
            Page<NotificationEntity> page = new Page<>(1, Integer.MAX_VALUE);
            return mapper.selectPage(page, queryWrapper);
        }
    }
}