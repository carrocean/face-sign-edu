package com.face.sign.user.service.impl;

import com.face.sign.common.util.SecurityUtils;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.user.mapper.UserMapper;
import com.face.sign.user.entity.UserEntity;
import com.face.sign.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserEntity login(String username, String password, String ip) {
        // 根据用户名查询用户
        UserEntity user = userMapper.selectUserByUsername(username);
        if (user != null && SecurityUtils.matchesPassword(password, user.getPassword())) {
            // 更新用户最后登录时间、IP和登录次数
            Date now = new Date();
            userMapper.updateUserLoginInfo(user.getUserId(), now, ip);
            return user;
        }
        return null;
    }

    @Override
    public boolean register(UserEntity user) {
        // 检查用户名是否已存在
        if (userMapper.checkUsernameExists(user.getUsername()) > 0) {
            throw new BizException("用户名已存在");
        }
        // 对密码进行加密
        user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
        // 插入新用户
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public UserEntity getUserById(Integer userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public boolean updateUser(UserEntity user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return userMapper.deleteUserById(userId) > 0;
    }

    @Override
    public boolean resetPassword(Integer userId, String newPassword) {
        UserEntity user = userMapper.selectUserById(userId);
        if (user != null) {
            if(SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
                throw new BizException("新密码与旧密码相同，请重新修改密码");
            }
            user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
            return userMapper.updateUser(user) > 0;
        }
        return false;
    }

    @Override
    public Boolean updateUserStatus(Integer userId, Integer status) {
        return userMapper.updateUserStatus(userId, status) > 0;
    }
}