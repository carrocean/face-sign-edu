package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.UserMapper;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserMapper> implements IUserService {

    @Autowired
    private UserMapper userMapper;

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
                    .set("last_login_ip", theUser.getLastLoginIp())
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

}