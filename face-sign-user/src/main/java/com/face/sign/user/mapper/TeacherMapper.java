package com.face.sign.user.mapper;

import com.face.sign.user.entity.TeacherEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper {
    @Select("SELECT * FROM teacher WHERE teacher_id = #{teacherId}")
    TeacherEntity getTeacherById(@Param("teacherId") Integer teacherId);

    @Select("SELECT * FROM teacher WHERE teacher_no = #{teacherNo}")
    TeacherEntity getTeacherByTeacherNo(@Param("teacherNo") String teacherNo);

    @Insert("INSERT INTO teacher (name, teacher_no, face_image_path, created_at, updated_at) " +
            "VALUES (#{name}, #{teacherNo}, #{faceImagePath}, #{createdAt}, #{updatedAt})")
    int addTeacher(TeacherEntity teacher);

    @Update("UPDATE teacher SET name = #{name}, face_image_path = #{faceImagePath}, " +
            "updated_at = #{updatedAt} WHERE teacher_id = #{teacherId}")
    int updateTeacher(TeacherEntity teacher);

    @Delete("DELETE FROM teacher WHERE teacher_id = #{teacherId}")
    int deleteTeacher(@Param("teacherId") Integer teacherId);

    @Select("SELECT * FROM teacher")
    List<TeacherEntity> getAllTeachers();
}