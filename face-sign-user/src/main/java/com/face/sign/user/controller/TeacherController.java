package com.face.sign.user.controller;

import com.alibaba.excel.EasyExcel;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.ExcelUtils;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.system.service.ISystemLogService;
import com.face.sign.user.bean.dto.ExcelTeacherDto;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.service.ITeacherService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/teacher")
public class TeacherController extends BaseController<TeacherEntity, ITeacherService> {

    @Autowired
    private ITeacherService teacherService;

    @Autowired
    ISystemLogService systemLogService;

    @GetMapping("/getByUserId/{id}")
    public JsonMsgDataBean getByUserId(@PathVariable Long id) {
        TeacherEntity entity = teacherService.getByUserId(id);
        systemLogService.logOperation(JwtUser.getUser().getId(), "查看教师信息", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(entity);
    }

    @PostMapping("/add")
    public JsonMsgDataBean addTeacher(@RequestBody TeacherEntity teacher) {
        teacherService.addTeacher(teacher);
        systemLogService.logOperation(JwtUser.getUser().getId(), "添加教师", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
    }

    @PostMapping("/import-teacher")
    public JsonMsgDataBean importTeachers(@RequestParam("file") MultipartFile file) {
        try {
            List<ExcelTeacherDto> teachers = EasyExcel.read(file.getInputStream(), ExcelTeacherDto.class, null).sheet().doReadSync();
            baseService.importTeachers(teachers);
            systemLogService.logOperation(JwtUser.getUser().getId(), "导入教师", IpUtils.getCurrentClientIp());
            return JsonMsgDataBean.buildSuccess();
        } catch (IOException e) {
            return JsonMsgDataBean.buildFail(e.getMessage());
        }
    }

    @PostMapping("/export-teacher")
    public void exportTeachers(HttpServletResponse response, @RequestBody(required = false) Map<String, Object> conditions, @RequestHeader String token) {
        List<ExcelTeacherDto> records = baseService.queryTeachers(conditions, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出教师", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "教师列表", ExcelTeacherDto.class, records);
    }
}