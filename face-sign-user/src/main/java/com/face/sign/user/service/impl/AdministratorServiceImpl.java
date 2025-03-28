package com.face.sign.user.service.impl;

import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.mapper.AdministratorMapper;
import com.face.sign.user.service.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministratorServiceImpl extends BaseServiceImpl<AdministratorEntity, AdministratorMapper> implements IAdministratorService {

    @Autowired
    private AdministratorMapper administratorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AdministratorEntity administrator) {
        // 检查管理员是否已存在
        if (administratorMapper.checkAdminExists(administrator.getUserId()) > 0) {
            throw new RuntimeException("该用户已经是管理员");
        }
        return super.save(administrator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AdministratorEntity administrator) {
        // 如果修改了用户ID，需要检查新用户ID是否已存在
        if (administrator.getUserId() != null) {
            AdministratorEntity oldAdmin = administratorMapper.selectById(administrator.getId());
            if (oldAdmin != null && !oldAdmin.getUserId().equals(administrator.getUserId())) {
                if (administratorMapper.checkAdminExists(administrator.getUserId()) > 0) {
                    throw new RuntimeException("该用户已经是管理员");
                }
            }
        }
        return super.updateById(administrator);
    }
}