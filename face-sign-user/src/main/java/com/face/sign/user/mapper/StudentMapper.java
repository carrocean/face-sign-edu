package com.face.sign.user.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.user.entity.StudentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends IBaseMapper<StudentEntity> {
    
    @Select("SELECT COUNT(*) FROM student WHERE student_number = #{studentNumber} AND has_delete = 0")
    int checkStudentNumberExists(String studentNumber);
}