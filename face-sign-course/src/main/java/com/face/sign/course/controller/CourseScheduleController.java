package com.face.sign.course.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.service.ICourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/course-schedule")
public class CourseScheduleController extends BaseController<CourseScheduleEntity, ICourseScheduleService> {

    ICourseScheduleService courseScheduleService;

    @Autowired
    public void setCourseScheduleService(ICourseScheduleService courseScheduleService) {
        this.courseScheduleService = courseScheduleService;
        init(courseScheduleService);
    }
}