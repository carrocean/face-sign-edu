package com.face.sign.service.impl;

import com.face.sign.entity.CourseEntity;
import com.face.sign.mapper.CourseMapper;
import com.face.sign.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CourseEntity addCourse(CourseEntity courseEntity) {
        courseMapper.addCourse(courseEntity);
        return courseEntity;
    }

    @Override
    public CourseEntity getCourseById(Long courseId) {
        return courseMapper.getCourseById(courseId);
    }

    @Override
    public CourseEntity updateCourse(CourseEntity courseEntity) {
        courseMapper.updateCourse(courseEntity);
        return courseEntity;
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseMapper.deleteCourse(courseId);
    }

    @Override
    public List<CourseEntity> getAllCourses() {
        return courseMapper.getAllCourses();
    }
}