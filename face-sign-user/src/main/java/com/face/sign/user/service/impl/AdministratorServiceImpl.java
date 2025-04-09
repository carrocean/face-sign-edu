package com.face.sign.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.mapper.AdministratorMapper;
import com.face.sign.user.service.IAdministratorService;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl extends BaseServiceImpl<AdministratorEntity, AdministratorMapper> implements IAdministratorService {


    @Override
    public AdministratorEntity getByUserId(Long id) {
        return getOne(Wrappers.<AdministratorEntity>lambdaQuery().eq(AdministratorEntity::getUserId, id));
    }
}