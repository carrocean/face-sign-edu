package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/face/sign/teacher")
public class TeacherController extends BaseController<TeacherEntity, ITeacherService> {

    @Autowired
    private ITeacherService teacherService;

    @GetMapping("/getByUserId/{id}")
    public JsonMsgDataBean getByUserId(@PathVariable Long id) {
        TeacherEntity entity = teacherService.getByUserId(id);
        return JsonMsgDataBean.buildSuccess(entity);
    }

    @PostMapping("/add")
    public JsonMsgDataBean addTeacher(@RequestBody TeacherEntity teacher) {
        teacherService.addTeacher(teacher);
        return JsonMsgDataBean.buildSuccess();
    }
}