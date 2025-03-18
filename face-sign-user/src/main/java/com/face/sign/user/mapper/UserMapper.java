package com.face.sign.user.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface UserMapper extends IBaseMapper<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象
     */
    UserEntity selectUserByUsername(String userName);

    /**
     * 验证用户名是否已存在
     *
     * @param userName 用户名
     * @return 存在返回1，不存在返回0
     */
    int checkUsernameExists(String userName);

    /**
     * 更新用户最后登录时间、IP和登录次数
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @param lastLoginIp ip
     * @return 影响行数
     */
    int updateUserLoginInfo(Long userId, Date lastLoginTime, String lastLoginIp);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 用户状态
     * @return 影响行数
     */
    int updateUserStatus(Long userId, Integer status);

}