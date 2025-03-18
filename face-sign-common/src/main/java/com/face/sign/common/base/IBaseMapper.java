package com.face.sign.common.base;


import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 工作基础类
 *
 */
public interface IBaseMapper<Entity> {
    int insert(Entity entity);

    int deleteById(Long id);

    int updateById(Entity entity);

    Entity selectById(Long id);

    List<Entity> selectAll();
}