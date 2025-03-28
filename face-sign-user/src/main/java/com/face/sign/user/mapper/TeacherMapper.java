package com.face.sign.user.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.user.entity.TeacherEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeacherMapper extends IBaseMapper<TeacherEntity> {
    
    @Select("SELECT COUNT(*) FROM teacher WHERE teacher_number = #{teacherNumber} AND has_delete = 0")
    int checkTeacherNumberExists(String teacherNumber);
}