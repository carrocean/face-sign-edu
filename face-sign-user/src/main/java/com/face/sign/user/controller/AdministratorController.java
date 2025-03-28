package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.user.entity.AdministratorEntity;
import com.face.sign.user.service.IAdministratorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/administrator")
public class AdministratorController extends BaseController<AdministratorEntity, IAdministratorService> {

}