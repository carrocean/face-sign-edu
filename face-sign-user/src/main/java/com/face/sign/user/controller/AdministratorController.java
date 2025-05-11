package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.IpUtils;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.common.util.jwt.JwtUser;
import com.face.sign.system.service.ISystemLogService;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.service.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/administrator")
public class AdministratorController extends BaseController<AdministratorEntity, IAdministratorService> {

    @Autowired
    private IAdministratorService administratorService;

    @Autowired
    ISystemLogService systemLogService;

    @GetMapping("/getByUserId/{id}")
    public JsonMsgDataBean getByUserId(@PathVariable Long id) {
        AdministratorEntity entity = administratorService.getByUserId(id);
        systemLogService.logOperation(JwtUser.getUser().getId(), "查看管理员信息", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess(entity);
    }

    @PostMapping("/add")
    public JsonMsgDataBean addAdministrator(@RequestBody AdministratorEntity administrator) {
        administratorService.addAdministrator(administrator);
        systemLogService.logOperation(JwtUser.getUser().getId(), "添加管理员", IpUtils.getCurrentClientIp());
        return JsonMsgDataBean.buildSuccess();
    }

}