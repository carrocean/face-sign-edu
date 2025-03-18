package com.face.sign.system.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.system.entity.SystemLogEntity;
import com.face.sign.system.mapper.SystemLogMapper;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemLogServiceImpl extends IBaseServiceImpl<SystemLogEntity, SystemLogMapper> implements ISystemLogService {

    private SystemLogMapper systemLogMapper;

    @Autowired
    public void setSystemLogMapper(SystemLogMapper systemLogMapper) {
        this.systemLogMapper = systemLogMapper;
        init(systemLogMapper);
    }
}