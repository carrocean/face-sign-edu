package com.face.sign.attendance.controller;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.JsonMsgDataBean;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/face/sign/attendance-record")
public class AttendanceRecordController extends BaseController<AttendanceRecordEntity, IAttendanceRecordService> {

    IAttendanceRecordService attendanceRecordService;

    @Autowired
    public void setAttendanceRecordService(IAttendanceRecordService attendanceRecordService) {
        this.attendanceRecordService = attendanceRecordService;
        init(attendanceRecordService);
    }


    /**
     * 人脸识别签到
     */
    @PostMapping("/sign-in")
    public JsonMsgDataBean signIn(@RequestParam MultipartFile image) {
        Boolean result = attendanceRecordService.signIn(111L, image);
        if (result) {
            return JsonMsgDataBean.buildSuccess("人脸验证成功");
        }
        return JsonMsgDataBean.buildFail("人脸验证失败");
    }


    /**
     * 导出考勤记录
     */
    @GetMapping("/export/{id}")
    public void exportAttendance(@PathVariable Long id, HttpServletResponse response) {
        attendanceRecordService.exportAttendance(id, response);
    }

    /**
     * 获取今日待签到课程
     */
    @GetMapping("/today-pending")
    public JsonMsgDataBean getTodayPendingCourses() {
        try {
            List<AttendanceRecordEntity> courses = attendanceRecordService.getTodayPendingCourses();
            return JsonMsgDataBean.buildSuccess(courses);
        } catch (Exception e) {
            return JsonMsgDataBean.buildFail("获取今日待签到课程失败");
        }
    }
}