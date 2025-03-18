package com.face.sign.course.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.mapper.CourseScheduleMapper;
import com.face.sign.course.service.ICourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseScheduleServiceImpl extends IBaseServiceImpl<CourseScheduleEntity, CourseScheduleMapper> implements ICourseScheduleService {

    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    public void setCourseScheduleMapper(CourseScheduleMapper courseScheduleMapper) {
        this.courseScheduleMapper = courseScheduleMapper;
        init(courseScheduleMapper);
    }
}