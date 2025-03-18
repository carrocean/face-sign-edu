package com.face.sign.mapper;

import com.face.sign.entity.CourseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("SELECT * FROM course WHERE course_id = #{courseId}")
    CourseEntity getCourseById(@Param("courseId") Long courseId);

    @Insert("INSERT INTO course (course_name, teacher_id, created_at, updated_at) " +
            "VALUES (#{courseName}, #{teacherId}, #{createdAt}, #{updatedAt})")
    int addCourse(CourseEntity course);

    @Update("UPDATE course SET course_name = #{courseName}, teacher_id = #{teacherId}, " +
            "updated_at = #{updatedAt} WHERE course_id = #{courseId}")
    int updateCourse(CourseEntity course);

    @Delete("DELETE FROM course WHERE course_id = #{courseId}")
    int deleteCourse(@Param("courseId") Long courseId);

    @Select("SELECT * FROM course")
    List<CourseEntity> getAllCourses();
}