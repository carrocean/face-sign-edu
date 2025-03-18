package com.face.sign.system.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.system.entity.SystemLogEntity;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/system-log")
public class SystemLogController extends BaseController<SystemLogEntity, ISystemLogService> {

    ISystemLogService systemLogService;

    @Autowired
    public void setSystemLogService(ISystemLogService systemLogService) {
        this.systemLogService = systemLogService;
        init(systemLogService);
    }
}