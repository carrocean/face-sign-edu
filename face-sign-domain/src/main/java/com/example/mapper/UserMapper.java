package com.face.sign.mapper;

import com.face.sign.entity.UserEntity;
import org.apache.ibatis.annotations.*;

public interface UserMapper {
    @Select("SELECT * FROM user WHERE user_name = #{userName} AND password = #{password} AND role = #{role}")
    UserEntity getUserByNameAndPassword(@Param("userName") String userName, @Param("password") String password, @Param("role") String role);

    @Select("SELECT * FROM user WHERE user_name = #{userName}")
    UserEntity getUserByName(@Param("userName") String userName);

    @Insert("INSERT INTO user (user_name, password, role, created_at, nick_name) " +
            "VALUES (#{userName}, #{password}, #{role}, #{createdAt}, #{nickName})")
    void save(UserEntity user);

    // 根据用户 ID 查询用户信息
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    UserEntity getUserById(@Param("userId") Integer userId);

    // 更新用户信息
    @Update("UPDATE user " +
            "SET user_name = #{userName}, " +
            "password = #{password}, " +
            "role = #{role}, " +
            "nick_name = #{nickName}, " +
            "updated_at = #{updatedAt} " +
            "WHERE user_id = #{userId}")
    void updateUser(UserEntity user);
}