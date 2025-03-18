package com.face.sign.user.service;
import com.face.sign.user.entity.UserEntity;
import java.util.List;

public interface IUserService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，若登录失败返回 null
     */
    UserEntity login(String username, String password);

    /**
     * 注册新用户
     *
     * @param user 用户对象
     * @return 注册是否成功
     */
    boolean register(UserEntity user);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户对象，若不存在返回 null
     */
    UserEntity getUserById(Integer userId);

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    List<UserEntity> getAllUsers();

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 更新是否成功
     */
    boolean updateUser(UserEntity user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteUser(Integer userId);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置是否成功
     */
    boolean resetPassword(Integer userId, String newPassword);

    /**
     * 验证用户名是否已存在
     *
     * @param username 用户名
     * @return 用户名已存在返回 true，不存在返回 false
     */
    boolean checkUsernameExists(String username);

}