package com.face.sign.user.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.user.entity.AdministratorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdministratorMapper extends IBaseMapper<AdministratorEntity> {
    
    @Select("SELECT COUNT(*) FROM administrator WHERE user_id = #{userId} AND has_delete = 0")
    int checkAdminExists(Long userId);
}