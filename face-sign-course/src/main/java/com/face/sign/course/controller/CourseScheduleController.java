package com.face.sign.course.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;
import com.face.sign.course.service.ICourseScheduleService;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/course-schedule")
public class CourseScheduleController extends BaseController<CourseScheduleEntity, ICourseScheduleService> {

    @Autowired
    ISystemLogService systemLogService;

    @PostMapping("/add")
    public JsonMsgDataBean addCourseSchedule(@RequestBody CourseScheduleVo courseScheduleVo) {
        baseService.addCourseSchedule(courseScheduleVo);
        systemLogService.logOperation(JwtUser.getUser().getId(), "添加课程", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
    }

    @PostMapping("/page-student")
    public JsonMsgDataBean getPageByStudent(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestParam(defaultValue = "true") boolean pageSearch,
            @RequestBody(required = false) Map<String, Object> searchForm,
            @RequestHeader String token) {
        Object pageResult = baseService.pageByStudent(currentPage, pageSize, pageSearch, fuzzySearch, searchForm, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览课程", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
    }

    @GetMapping("/today-student")
    public JsonMsgDataBean getTodayCourseStudent(@RequestHeader String token) {
        Object result = baseService.getTodayCourseStudent(token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "查看今日课程", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(result);
    }

}