package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("SELECT * FROM course WHERE course_id = #{courseId}")
    Course getCourseById(@Param("courseId") Long courseId);

    @Insert("INSERT INTO course (course_name, teacher_id, created_at, updated_at) " +
            "VALUES (#{courseName}, #{teacherId}, #{createdAt}, #{updatedAt})")
    int addCourse(Course course);

    @Update("UPDATE course SET course_name = #{courseName}, teacher_id = #{teacherId}, " +
            "updated_at = #{updatedAt} WHERE course_id = #{courseId}")
    int updateCourse(Course course);

    @Delete("DELETE FROM course WHERE course_id = #{courseId}")
    int deleteCourse(@Param("courseId") Long courseId);

    @Select("SELECT * FROM course")
    List<Course> getAllCourses();
}