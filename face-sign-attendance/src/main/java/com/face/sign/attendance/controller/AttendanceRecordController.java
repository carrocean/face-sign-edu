package com.face.sign.attendance.controller;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseController;
import com.face.sign.common.util.JsonMsgDataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/face/sign/attendance-record")
public class AttendanceRecordController extends BaseController<AttendanceRecordEntity, IAttendanceRecordService> {

    @Autowired
    IAttendanceRecordService attendanceRecordService;


    /**
     * 人脸识别签到
     */
    @PostMapping("/sign-in")
    public JsonMsgDataBean signIn(@RequestParam MultipartFile image) {
        Boolean result = attendanceRecordService.signIn(111L, image);
        if (result) {
            return JsonMsgDataBean.buildSuccess("人脸验证成功");
        }
        return JsonMsgDataBean.buildFail("人脸验证失败");
    }
}