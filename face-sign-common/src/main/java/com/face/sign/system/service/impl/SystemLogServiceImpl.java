package com.face.sign.system.service.impl;

import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.system.entity.SystemLogEntity;
import com.face.sign.system.mapper.SystemLogMapper;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SystemLogServiceImpl extends BaseServiceImpl<SystemLogEntity, SystemLogMapper> implements ISystemLogService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public void logOperation(String userId, String operation, String ipAddress) {
        SystemLogEntity systemLog = new SystemLogEntity();
        systemLog.setUserId(Long.valueOf(userId));
        systemLog.setOperation(operation);
        systemLog.setOperationTime(new Date());
        systemLog.setIpAddress(ipAddress);
        systemLog.setAddTime(new Date());
        systemLog.setUpdateTime(new Date());

        systemLogMapper.insert(systemLog);
    }
}