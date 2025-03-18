package com.face.sign.user.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.mapper.AdministratorMapper;
import com.face.sign.user.service.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl extends IBaseServiceImpl<AdministratorEntity, AdministratorMapper> implements IAdministratorService {

    private AdministratorMapper administratorMapper;

    @Autowired
    public void setAdministratorMapper(AdministratorMapper administratorMapper) {
        this.administratorMapper = administratorMapper;
        init(administratorMapper);
    }
}