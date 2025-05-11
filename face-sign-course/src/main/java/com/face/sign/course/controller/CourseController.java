package com.face.sign.course.controller;

import com.alibaba.excel.EasyExcel;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.ExcelUtils;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.course.bean.dto.ExcelCourseDto;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.service.ICourseService;
import com.face.sign.system.service.ISystemLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/course")
public class CourseController extends BaseController<CourseEntity, ICourseService> {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ISystemLogService systemLogService;

    @PostMapping("/import-course")
    public JsonMsgDataBean importCourses(@RequestParam("file") MultipartFile file) {
        try {
            List<ExcelCourseDto> courses = EasyExcel.read(file.getInputStream(), ExcelCourseDto.class, null).sheet().doReadSync();
            courseService.importCourses(courses);
            systemLogService.logOperation(JwtUser.getUser().getId(), "导入课程", IpUtils.getCurrentClientIp());
            return JsonMsgDataBean.buildSuccess();
        } catch (Exception e) {
            return JsonMsgDataBean.buildFail(e.getMessage());
        }
    }

    @PostMapping("/export-course")
    public void exportCourses(HttpServletResponse response, @RequestBody(required = false) Map<String, Object> conditions, @RequestHeader String token) {
        List<ExcelCourseDto> records = courseService.exportCourses(conditions, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出课程", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "课程列表", ExcelCourseDto.class, records);
    }
}