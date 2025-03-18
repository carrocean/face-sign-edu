package com.face.sign.service.impl;

import com.face.sign.entity.UserEntity;
import com.face.sign.mapper.UserMapper;
import com.face.sign.service.IUserService;
import com.face.sign.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public UserEntity loginService(UserEntity newUser) {
        // 从数据库中获取用户信息
        UserEntity dbUser = userMapper.getUserByNameAndPassword(newUser.getUserName(), newUser.getPassword(), newUser.getRole());

        if (dbUser != null) {
            // 如果用户存在，生成 JWT Token
            String token = JwtUtil.createToken(dbUser.getUserId(), dbUser.getUserName());
            dbUser.setToken(token);
            // 更新最后登录时间
            dbUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateUser(dbUser); // 假设有一个 update 方法
        }

        return dbUser;
    }

    @Override
    public String registerService(UserEntity newUser) {
        // 检查用户名是否已存在
        UserEntity user = userMapper.getUserByName(newUser.getUserName());
        if (user == null) {
            // 注册新用户
            newUser.setCreatedAt(LocalDateTime.now());
            userMapper.save(newUser);
            return "注册成功";
        } else {
            return "注册失败：用户名已存在";
        }
    }

    @Override
    public Boolean checkTokenService(HttpServletRequest request){
        String token=request.getHeader("token");
        return JwtUtil.checkToken(token);
    }

    @Override
    public UserEntity getById(Integer userId) {
        return userMapper.getUserById(userId);
    }

}
