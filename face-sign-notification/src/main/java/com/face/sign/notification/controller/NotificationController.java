package com.face.sign.notification.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.notification.entity.NotificationEntity;
import com.face.sign.notification.service.INotificationService;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/notification")
public class NotificationController extends BaseController<NotificationEntity, INotificationService> {

    @Autowired
    ISystemLogService systemLogService;

    @PostMapping("/page-student")
    public JsonMsgDataBean getPageByStudent(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestParam(defaultValue = "true") boolean pageSearch,
            @RequestBody(required = false) Map<String, Object> searchForm,
            @RequestHeader String token) {
        Object pageResult = baseService.pageByStudent(currentPage, pageSize, pageSearch, fuzzySearch, searchForm, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览通知", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
    }

    @PostMapping("/page-teacher")
    public JsonMsgDataBean getPageByTeacher(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "true") boolean fuzzySearch,
            @RequestParam(defaultValue = "true") boolean pageSearch,
            @RequestBody(required = false) Map<String, Object> searchForm,
            @RequestHeader String token) {
        Object pageResult = baseService.pageByTeacher(currentPage, pageSize, pageSearch, fuzzySearch, searchForm, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览通知", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
    }
}