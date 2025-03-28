package com.face.sign.notification.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.notification.entity.NotificationEntity;
import com.face.sign.notification.service.INotificationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/notification")
public class NotificationController extends BaseController<NotificationEntity, INotificationService> {

}