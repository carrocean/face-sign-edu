package com.example.service;

import com.example.entity.Course;

import java.util.List;

public interface ICourseService {
    // 添加课程信息
    Course addCourse(Course course);

    // 根据课程ID获取课程信息
    Course getCourseById(Long courseId);

    // 更新课程信息
    Course updateCourse(Course course);

    // 删除课程信息
    void deleteCourse(Long courseId);

    // 获取所有课程信息
    List<Course> getAllCourses();
}