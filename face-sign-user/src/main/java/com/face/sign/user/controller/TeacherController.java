package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.service.ITeacherService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/teacher")
public class TeacherController extends BaseController<TeacherEntity, ITeacherService> {
}