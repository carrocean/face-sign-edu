package com.face.sign.user.controller;

import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.JsonMsgDataBean;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face/sign/student")
public class StudentController extends BaseController<StudentEntity, IStudentService> {

    @Autowired
    private IStudentService studentService;

    @GetMapping("/getByUserId/{id}")
    public JsonMsgDataBean getByUserId(@PathVariable Long id) {
        StudentEntity entity = studentService.getByUserId(id);
        return JsonMsgDataBean.buildSuccess(entity);
    }

    @PostMapping("/add")
    public JsonMsgDataBean addStudent(@RequestBody StudentEntity student) {
        studentService.addStudent(student);
        return JsonMsgDataBean.buildSuccess();
    }

}