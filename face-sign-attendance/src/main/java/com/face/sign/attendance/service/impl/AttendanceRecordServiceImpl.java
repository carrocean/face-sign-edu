package com.face.sign.attendance.service.impl;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.mapper.AttendanceRecordMapper;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.IBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceRecordServiceImpl extends IBaseServiceImpl<AttendanceRecordEntity, AttendanceRecordMapper> implements IAttendanceRecordService {

    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    public void setAttendanceRecordMapper(AttendanceRecordMapper attendanceRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
        init(attendanceRecordMapper);
    }
}