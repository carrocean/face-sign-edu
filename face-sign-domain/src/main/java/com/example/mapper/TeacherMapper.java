package com.face.sign.mapper;

import com.face.sign.entity.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {
    @Select("SELECT * FROM teacher WHERE teacher_id = #{teacherId}")
    Teacher getTeacherById(@Param("teacherId") Long teacherId);

    @Select("SELECT * FROM teacher WHERE teacher_no = #{teacherNo}")
    Teacher getTeacherByTeacherNo(@Param("teacherNo") String teacherNo);

    @Insert("INSERT INTO teacher (name, teacher_no, face_image_path, created_at, updated_at) " +
            "VALUES (#{name}, #{teacherNo}, #{faceImagePath}, #{createdAt}, #{updatedAt})")
    int addTeacher(Teacher teacher);

    @Update("UPDATE teacher SET name = #{name}, face_image_path = #{faceImagePath}, " +
            "updated_at = #{updatedAt} WHERE teacher_id = #{teacherId}")
    int updateTeacher(Teacher teacher);

    @Delete("DELETE FROM teacher WHERE teacher_id = #{teacherId}")
    int deleteTeacher(@Param("teacherId") Long teacherId);

    @Select("SELECT * FROM teacher")
    List<Teacher> getAllTeachers();
}