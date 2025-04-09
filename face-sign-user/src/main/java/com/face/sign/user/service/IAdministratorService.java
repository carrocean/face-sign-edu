package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.entity.AdministratorEntity;

public interface IAdministratorService extends IBaseService<AdministratorEntity> {

    /**
     * 根据用户ID获取用户详情
     * @param id
     * @return
     */
    AdministratorEntity getByUserId(Long id);
}