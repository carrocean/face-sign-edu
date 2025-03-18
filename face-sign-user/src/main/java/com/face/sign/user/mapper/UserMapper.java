package com.face.sign.user.mapper;

import com.face.sign.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    UserEntity selectUserByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    UserEntity selectUserById(Integer userId);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<UserEntity> selectAllUsers();

    /**
     * 插入新用户
     *
     * @param user 用户对象
     * @return 影响行数
     */
    int insertUser(UserEntity user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 影响行数
     */
    int updateUser(UserEntity user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserById(Integer userId);

    /**
     * 验证用户名是否已存在
     *
     * @param username 用户名
     * @return 存在返回1，不存在返回0
     */
    int checkUsernameExists(String username);

    /**
     * 更新用户最后登录时间、IP和登录次数
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp ip
     * @return 影响行数
     */
    int updateUserLoginInfo(Integer userId, Date lastLoginTime, String lastLoginIp);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 用户状态
     * @return 影响行数
     */
    int updateUserStatus(Integer userId, Integer status);

}