package com.face.sign.attendance.service.impl;

import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.attendance.mapper.LeaveRequestMapper;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.IBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveRequestServiceImpl extends IBaseServiceImpl<LeaveRequestEntity, LeaveRequestMapper> implements ILeaveRequestService {

    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    public void setLeaveRequestMapper(LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestMapper = leaveRequestMapper;
        init(leaveRequestMapper);
    }
}