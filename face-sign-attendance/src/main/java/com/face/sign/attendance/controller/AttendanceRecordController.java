package com.face.sign.attendance.controller;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/attendance-record")
public class AttendanceRecordController extends BaseController<AttendanceRecordEntity, IAttendanceRecordService> {

    IAttendanceRecordService attendanceRecordService;

    @Autowired
    public void setAttendanceRecordService(IAttendanceRecordService attendanceRecordService) {
        this.attendanceRecordService = attendanceRecordService;
        init(attendanceRecordService);
    }
}