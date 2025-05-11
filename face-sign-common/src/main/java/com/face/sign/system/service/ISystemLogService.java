package com.face.sign.system.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.system.entity.SystemLogEntity;

public interface ISystemLogService extends IBaseService<SystemLogEntity> {

    /**
     * 记录系统操作日志
     * @param userId 用户ID
     * @param operation 操作描述
     * @param ipAddress 操作IP地址
     */
    public void logOperation(String userId, String operation, String ipAddress);


}