package com.example.service;

import com.example.entity.Attendance;

import java.util.List;

public interface IAttendanceService {
    // 添加考勤记录
    Attendance addAttendance(Attendance attendance);

    // 根据考勤记录ID获取考勤记录
    Attendance getAttendanceById(Long attendanceId);

    // 更新考勤记录
    Attendance updateAttendance(Attendance attendance);

    // 删除考勤记录
    void deleteAttendance(Long attendanceId);

    // 根据学生ID和课程ID获取考勤记录
    List<Attendance> getAttendanceByStudentIdAndCourseId(Long studentId, Long courseId);

    // 获取所有考勤记录
    List<Attendance> getAllAttendance();
}