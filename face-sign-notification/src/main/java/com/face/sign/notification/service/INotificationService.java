package com.face.sign.notification.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.notification.entity.NotificationEntity;

import java.util.Map;

public interface INotificationService extends IBaseService<NotificationEntity> {

    /**
     *
     * 根据学生获取通知分页列表
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param searchForm
     * @return
     */
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> searchForm, String token);

    /**
     * 根据老师获取通知分页列表
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param searchForm
     * @param token
     * @return
     */
    Object pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> searchForm, String token);
}