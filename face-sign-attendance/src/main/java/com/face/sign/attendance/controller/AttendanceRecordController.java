package com.face.sign.attendance.controller;

import com.face.sign.attendance.bean.dto.AttendanceRecordDTO;
import com.face.sign.attendance.bean.vo.StudentCountVo;
import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.ExcelUtils;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.system.service.ISystemLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/attendance-record")
public class AttendanceRecordController extends BaseController<AttendanceRecordEntity, IAttendanceRecordService> {

    @Autowired
    IAttendanceRecordService attendanceRecordService;

    @Autowired
    ISystemLogService systemLogService;


    /**
     * 人脸识别签到
     */
    @PostMapping("/sign-in")
    public JsonMsgDataBean signIn(@RequestParam MultipartFile image,
                                  @RequestParam("scheduleId") String scheduleId,
                                  @RequestParam("courseId") String courseId,
                                  @RequestHeader String token) {
        Boolean result = attendanceRecordService.signIn(image, Long.valueOf(scheduleId), Long.valueOf(courseId), token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "签到", IpUtils.getCurrentClientIp());
        if (result) {
            return JsonMsgDataBean.buildSuccess("人脸验证成功");
        }
        return JsonMsgDataBean.buildFail("人脸验证失败");
    }


    /**
     * 上传照片
     * @param userId
     * @param file
     * @return
     */
    @PostMapping("/upload/{userId}")
    public JsonMsgDataBean upload(@PathVariable String userId, MultipartFile file) {
        attendanceRecordService.uploadImage(userId, file);
        systemLogService.logOperation(JwtUser.getUser().getId(), "上传人脸照片", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(userId);
    }

    /**
     * 预览学生照片
     * @param userId
     * @return
     */
    @GetMapping("/preview/{userId}")
    public JsonMsgDataBean preview(@PathVariable String userId) {
        String url = attendanceRecordService.preview(userId);
        systemLogService.logOperation(JwtUser.getUser().getId(), "查询学生照片", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(url);
    }

    @PostMapping("/page-teacher")
    public JsonMsgDataBean getPageByTeacher(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestParam(defaultValue = "true") boolean pageSearch,
            @RequestBody(required = false) Map<String, Object> searchForm,
            @RequestHeader String token) {
        Object pageResult = baseService.pageByTeacher(currentPage, pageSize, pageSearch, fuzzySearch, searchForm, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "获取考勤列表", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
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
        systemLogService.logOperation(JwtUser.getUser().getId(), "获取考勤列表", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
    }

    @PostMapping("/export-teacher")
    public void exportTeacherAttendanceRecords(HttpServletResponse response, @RequestBody(required = false) Map<String, Object> conditions, @RequestHeader String token) {
        List<AttendanceRecordDTO> records = attendanceRecordService.queryTeacherAttendanceRecords(conditions, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出考勤记录", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "考勤记录", AttendanceRecordDTO.class, records);
    }

    @PostMapping("/export-admin")
    public void exportAdminAttendanceRecords(HttpServletResponse response, @RequestBody(required = false) Map<String, Object> conditions, @RequestHeader String token) {
        List<AttendanceRecordDTO> records = attendanceRecordService.queryAdminAttendanceRecords(conditions, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "导出考勤记录", IpUtils.getCurrentClientIp());
        ExcelUtils.exportExcel(response, "考勤记录", AttendanceRecordDTO.class, records);
    }

    @GetMapping("/count-student")
    public StudentCountVo getStudentCount(@RequestHeader String token) {
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览主页", IpUtils.getCurrentClientIp());
        return attendanceRecordService.getStudentCount(token);
    }

}