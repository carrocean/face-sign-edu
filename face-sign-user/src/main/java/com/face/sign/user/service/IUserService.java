package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.entity.UserEntity;

public interface IUserService extends IBaseService<UserEntity> {

    /**
     * 用户登录
     *
     * @param user 用户对象
     * @return 登录成功的用户对象，若登录失败返回 null
     */
    UserEntity login(UserEntity user);

    /**
     * 注册新用户
     *
     * @param user 用户对象
     * @return 注册是否成功
     */
    int register(UserEntity user);

}