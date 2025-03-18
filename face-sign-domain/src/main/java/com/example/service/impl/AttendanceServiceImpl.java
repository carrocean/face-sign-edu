package com.face.sign.service.impl;

import com.face.sign.entity.Attendance;
import com.face.sign.mapper.AttendanceMapper;
import com.face.sign.service.IAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements IAttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public Attendance addAttendance(Attendance attendance) {
        attendanceMapper.addAttendance(attendance);
        return attendance;
    }

    @Override
    public Attendance getAttendanceById(Long attendanceId) {
        return attendanceMapper.getAttendanceById(attendanceId);
    }

    @Override
    public Attendance updateAttendance(Attendance attendance) {
        attendanceMapper.updateAttendance(attendance);
        return attendance;
    }

    @Override
    public void deleteAttendance(Long attendanceId) {
        attendanceMapper.deleteAttendance(attendanceId);
    }

    @Override
    public List<Attendance> getAttendanceByStudentIdAndCourseId(Long studentId, Long courseId) {
        return attendanceMapper.getAttendanceByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceMapper.getAllAttendance();
    }
}