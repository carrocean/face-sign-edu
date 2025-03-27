package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.entity.UserEntity;

public interface IUserService extends IBaseService<UserEntity> {

    /**
     * 用户登录
     *
     * @param account 账号
     * @param password 密码
     * @return 登录成功的用户对象，若登录失败返回 null
     */
    UserEntity login(String account, String password, String ip);

    /**
     * 注册新用户
     *
     * @param user 用户对象
     * @return 注册是否成功
     */
    int register(UserEntity user);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置是否成功
     */
    int resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 用户状态
     * @return 是否更新成功
     */
    int updateUserStatus(Long userId, Integer status);

}