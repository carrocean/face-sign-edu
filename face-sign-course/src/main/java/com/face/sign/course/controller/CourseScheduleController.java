package com.face.sign.course.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;
import com.face.sign.course.service.ICourseScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/course-schedule")
public class CourseScheduleController extends BaseController<CourseScheduleEntity, ICourseScheduleService> {



    @PostMapping("/add")
    public JsonMsgDataBean addCourseSchedule(@RequestBody CourseScheduleVo courseScheduleVo) {
        baseService.addCourseSchedule(courseScheduleVo);
        return JsonMsgDataBean.buildSuccess();
    }

}