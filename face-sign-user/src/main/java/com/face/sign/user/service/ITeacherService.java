package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.entity.TeacherEntity;

public interface ITeacherService extends IBaseService<TeacherEntity> {

    /**
     * 根据用户ID获取用户详情
     * @param id
     * @return
     */
    TeacherEntity getByUserId(Long id);
}