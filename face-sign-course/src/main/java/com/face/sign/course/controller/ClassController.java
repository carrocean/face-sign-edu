package com.face.sign.course.controller;

import com.alibaba.excel.EasyExcel;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.ExcelUtils;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.course.bean.dto.ExcelStudentDto;
import com.face.sign.course.entity.ClassEntity;
import com.face.sign.course.service.IClassService;
import com.face.sign.system.service.ISystemLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/class")
public class ClassController extends BaseController<ClassEntity, IClassService> {

    @Autowired
    private IClassService classService;

    @Autowired
    private ISystemLogService systemLogService;

    @PostMapping("/import-student")
    public JsonMsgDataBean importStudents(@RequestParam("file") MultipartFile file) {
        try {
            List<ExcelStudentDto> students = EasyExcel.read(file.getInputStream(), ExcelStudentDto.class, null).sheet().doReadSync();
            classService.importStudents(students);
            systemLogService.logOperation(JwtUser.getUser().getId(), "导入学生", IpUtils.getCurrentClientIp());
            return JsonMsgDataBean.buildSuccess();
        } catch (IOException e) {
            return JsonMsgDataBean.buildFail(e.getMessage());
        }
    }

    @PostMapping("/export-student")
    public void exportStudents(HttpServletResponse response, @RequestBody(required = false) Map<String, Object> conditions, @RequestHeader String token) {
        List<ExcelStudentDto> records = classService.queryStudents(conditions, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出学生", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "学生列表", ExcelStudentDto.class, records);
    }
}