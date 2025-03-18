package com.face.sign.common.base;


import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 工作基础类
 *
 */
public interface IBaseMapper<Entity> {
    @Insert("INSERT INTO ${table} (${columns}) VALUES (${values})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Entity entity);

    @Delete("DELETE FROM ${table} WHERE id = #{id}")
    void deleteById(Integer id);

    @Update("UPDATE ${table} SET ${updates} WHERE id = #{id}")
    void updateById(Entity entity);

    @Select("SELECT * FROM ${table} WHERE id = #{id}")
    Entity selectById(Integer id);

    @Select("SELECT * FROM ${table}")
    List<Entity> selectAll();
}