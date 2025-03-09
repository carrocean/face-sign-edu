package com.example.mapper;

import com.example.entity.Class;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper {
    @Select("SELECT * FROM class WHERE class_id = #{classId}")
    Class getClassById(@Param("classId") Long classId);

    @Insert("INSERT INTO class (class_name, created_at, updated_at) " +
            "VALUES (#{className}, #{createdAt}, #{updatedAt})")
    int addClass(Class classEntity);

    @Update("UPDATE class SET class_name = #{className}, updated_at = #{updatedAt} " +
            "WHERE class_id = #{classId}")
    int updateClass(Class classEntity);

    @Delete("DELETE FROM class WHERE class_id = #{classId}")
    int deleteClass(@Param("classId") Long classId);

    @Select("SELECT * FROM class")
    List<Class> getAllClasses();
}