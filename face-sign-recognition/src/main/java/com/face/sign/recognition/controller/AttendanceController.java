package com.face.sign.recognition.controller;

import com.face.sign.recognition.service.IAttendanceService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/face/sign/attendance")
public class AttendanceController {

    Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private IAttendanceService attendanceService;


    // 检测人脸
    @PostMapping("/detect")
    public ResponseEntity<String> detectFace(@RequestParam MultipartFile image) throws IOException {
        String imageBase64 = convertFileToBase64(image);
        logger.info(imageBase64);
        JSONObject result = attendanceService.detectFace(imageBase64);
        return ResponseEntity.ok(result.toString());
    }

    // 注册人脸
    @PostMapping("/register")
    public ResponseEntity<String> registerFace(@RequestParam("userId") String userId,
                                               @RequestParam MultipartFile image,
                                               @RequestParam("groupId") String groupId) throws IOException {
        String imageBase64 = convertFileToBase64(image);
        JSONObject result = attendanceService.registerFace(userId, imageBase64, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 人脸识别搜索
    @PostMapping("/search")
    public ResponseEntity<String> searchFace(@RequestParam MultipartFile image,
                                             @RequestParam("groupId") String groupId) throws IOException {
        String imageBase64 = convertFileToBase64(image);
        JSONObject result = attendanceService.searchFace(imageBase64, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 更新人脸
    @PostMapping("/update")
    public ResponseEntity<String> updateFace(@RequestParam("userId") String userId,
                                             @RequestParam MultipartFile image,
                                             @RequestParam("groupId") String groupId) throws IOException {
        String imageBase64 = convertFileToBase64(image);
        JSONObject result = attendanceService.updateFace(userId, imageBase64, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 删除人脸
    @PostMapping("/delete-face")
    public ResponseEntity<String> deleteFace(@RequestParam("userId") String userId,
                                             @RequestParam("faceToken") String faceToken,
                                             @RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.deleteFace(userId, faceToken, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 获取用户信息
    @GetMapping("/user-info")
    public ResponseEntity<String> getUserInfo(@RequestParam("userId") String userId,
                                              @RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.getUserInfo(userId, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 获取用户人脸列表
    @GetMapping("/user-face-list")
    public ResponseEntity<String> getUserFaceList(@RequestParam("userId") String userId,
                                                  @RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.getUserFaceList(userId, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 获取用户列表
    @GetMapping("/user-list")
    public ResponseEntity<String> getUserList(@RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.getUserList(groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 删除用户
    @PostMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam("userId") String userId,
                                             @RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.deleteUser(userId, groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 创建用户组
    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.createGroup(groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 删除用户组
    @PostMapping("/delete-group")
    public ResponseEntity<String> deleteGroup(@RequestParam("groupId") String groupId) {
        JSONObject result = attendanceService.deleteGroup(groupId);
        return ResponseEntity.ok(result.toString());
    }

    // 获取用户组列表
    @GetMapping("/group-list")
    public ResponseEntity<String> getGroupList() {
        JSONObject result = attendanceService.getGroupList();
        return ResponseEntity.ok(result.toString());
    }

    // 工具方法：将文件转换为Base64字符串
    private String convertFileToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}