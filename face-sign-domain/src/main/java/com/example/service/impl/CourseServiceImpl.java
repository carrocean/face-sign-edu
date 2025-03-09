package com.example.service.impl;

import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Course addCourse(Course course) {
        courseMapper.addCourse(course);
        return course;
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseMapper.getCourseById(courseId);
    }

    @Override
    public Course updateCourse(Course course) {
        courseMapper.updateCourse(course);
        return course;
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseMapper.deleteCourse(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseMapper.getAllCourses();
    }
}