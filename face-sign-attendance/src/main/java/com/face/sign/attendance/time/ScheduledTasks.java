package com.face.sign.attendance.time;

import com.face.sign.attendance.service.IAttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 考勤定时任务
 */
@Component
public class ScheduledTasks {

    @Autowired
    private IAttendanceRecordService attendanceRecordService;

    // 每天 9:40 执行
    @Scheduled(cron = "0 40 9 * * ?")
    public void executeTaskAt940() {
        attendanceRecordService.handleAttendanceRecords();
    }

    // 每天 11:45 执行
    @Scheduled(cron = "0 45 11 * * ?")
    public void executeTaskAt1140() {
        attendanceRecordService.handleAttendanceRecords();
    }

    // 每天 16:05 执行
    @Scheduled(cron = "0 5 16 * * ?")
    public void executeTaskAt1605() {
        attendanceRecordService.handleAttendanceRecords();
    }

    // 每天 18:00 执行
    @Scheduled(cron = "0 0 18 * * ?")
    public void executeTaskAt1800() {
        attendanceRecordService.handleAttendanceRecords();
    }

}