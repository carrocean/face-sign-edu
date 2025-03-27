package com.face.sign.user.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.mapper.UserMapper;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl extends IBaseServiceImpl<UserEntity, UserMapper> implements IUserService {

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
        init(userMapper);
    }

    @Override
    public UserEntity login(String account, String password, String ip) {
        // 根据账号查询用户
        UserEntity user = userMapper.selectUserByAccount(account);
        if (user != null && SecurityUtils.matchesPassword(password, user.getPassword())) {
            // 更新用户最后登录时间、IP和登录次数
            Date now = new Date();
            userMapper.updateUserLoginInfo(user.getId(), now, ip);
            return user;
        }
        return null;
    }

    @Override
    public int register(UserEntity user) {
        // 检查账号是否已存在
        if (userMapper.checkAccountExists(user.getAccount()) > 0) {
            throw new BizException("账号已存在");
        }
        // 对密码进行加密
        user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
        // 插入新用户
        return userMapper.insert(user);
    }

    @Override
    public int resetPassword(Long userId, String newPassword) {
        UserEntity user = userMapper.selectById(userId);
        if (user != null) {
            if(SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
                throw new BizException("新密码与旧密码相同，请重新修改密码");
            }
            user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
            return userMapper.updateById(user);
        }
        return 0;
    }

    @Override
    public int updateUserStatus(Long userId, Integer status) {
        return userMapper.updateUserStatus(userId, status);
    }
}