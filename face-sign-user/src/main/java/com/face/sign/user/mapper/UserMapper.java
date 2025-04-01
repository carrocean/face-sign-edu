package com.face.sign.user.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends IBaseMapper<UserEntity> {
}