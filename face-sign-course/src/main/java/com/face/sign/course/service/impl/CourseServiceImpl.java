package com.face.sign.course.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.mapper.CourseMapper;
import com.face.sign.course.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends IBaseServiceImpl<CourseEntity, CourseMapper> implements ICourseService {

    private CourseMapper courseMapper;

    @Autowired
    public void setCourseMapper(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
        init(courseMapper);
    }
}