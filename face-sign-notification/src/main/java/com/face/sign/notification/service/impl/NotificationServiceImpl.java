package com.face.sign.notification.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.notification.entity.NotificationEntity;
import com.face.sign.notification.mapper.NotificationMapper;
import com.face.sign.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends IBaseServiceImpl<NotificationEntity, NotificationMapper> implements INotificationService {

    private NotificationMapper notificationMapper;

    @Autowired
    public void setNotificationMapper(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
        init(notificationMapper);
    }
}