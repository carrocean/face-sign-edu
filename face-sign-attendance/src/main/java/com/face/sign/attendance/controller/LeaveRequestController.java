package com.face.sign.attendance.controller;

import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/leave-request")
public class LeaveRequestController extends BaseController<LeaveRequestEntity, ILeaveRequestService> {

    ILeaveRequestService leaveRequestService;

    @Autowired
    public void setLeaveRequestService(ILeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
        init(leaveRequestService);
    }
}