package com.face.sign.course.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.course.entity.ClassEntity;
import com.face.sign.course.service.IClassService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/class")
public class ClassController extends BaseController<ClassEntity, IClassService> {

}