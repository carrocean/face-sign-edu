package com.face.sign.course.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.service.ICourseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/course")
public class CourseController extends BaseController<CourseEntity, ICourseService> {

}