package com.face.sign.attendance.controller;

import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.system.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/face/sign/leave-request")
public class LeaveRequestController extends BaseController<LeaveRequestEntity, ILeaveRequestService> {

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
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览请假列表", IpUtils.getCurrentClientIp());
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
        systemLogService.logOperation(JwtUser.getUser().getId(), "浏览请假列表", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(pageResult);
    }

    @PostMapping("/save-student")
    public JsonMsgDataBean saveByStudent(@RequestBody LeaveRequestEntity entity, @RequestHeader String token) {
        baseService.saveByStudent(entity, token);
        systemLogService.logOperation(JwtUser.getUser().getId(), "请假申请", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
    }
}