package com.face.sign.recognition.controller;

import com.face.sign.recognition.service.FaceAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private FaceAttendanceService faceAttendanceService;

    /**
     * 人脸识别
     */
    @PostMapping("/check")
    public Map<String, Object> checkAttendance(@RequestParam String imageBase64, @RequestParam String groupIdList) throws IOException {
        return faceAttendanceService.searchFace(imageBase64, groupIdList);
    }


    /**
     * 注册人脸
     */
    @PostMapping("/face")
    public Map<String, Object> addFace(
            @RequestParam String imageBase64,
            @RequestParam String groupId,
            @RequestParam String userId,
            @RequestParam String userInfo) throws IOException {
        return faceAttendanceService.addFace(imageBase64, groupId, userId, userInfo);
    }

    /**
     * 删除人脸
     */
    @DeleteMapping("/face")
    public Map<String, Object> deleteFace(
            @RequestParam String groupId,
            @RequestParam String userId,
            @RequestParam String faceToken) throws IOException {
        return faceAttendanceService.deleteFace(groupId, userId, faceToken);
    }

    /**
     * 更新人脸信息
     */
    @PutMapping("/face")
    public Map<String, Object> updateFace(
            @RequestParam String imageBase64,
            @RequestParam String groupId,
            @RequestParam String userId,
            @RequestParam String userInfo) throws IOException {
        return faceAttendanceService.updateFace(imageBase64, groupId, userId, userInfo);
    }

    /**
     * 创建人脸库
     */
    @PostMapping("/group")
    public Map<String, Object> createGroup(
            @RequestParam String groupId,
            @RequestParam String groupName) throws IOException {
        return faceAttendanceService.createGroup(groupId, groupName);
    }

    /**
     * 删除人脸库
     */
    @DeleteMapping("/group")
    public Map<String, Object> deleteGroup(@RequestParam String groupId) throws IOException {
        return faceAttendanceService.deleteGroup(groupId);
    }

    /**
     * 批量注册人脸
     */
    @PostMapping("/faces/batch")
    public Map<String, Object> batchAddFaces(
            @RequestBody List<Map<String, String>> faceList,
            @RequestParam String groupId) throws IOException {
        return faceAttendanceService.batchAddFaces(faceList, groupId);
    }

    /**
     * 获取所有用户组列表
     */
    @GetMapping("/groups")
    public Map<String, Object> getGroupList() throws IOException {
        return faceAttendanceService.getGroupList();
    }

    /**
     * 获取用户组中的用户列表
     */
    @GetMapping("/users")
    public Map<String, Object> getUserList(@RequestParam String groupId) throws IOException {
        return faceAttendanceService.getUserList(groupId);
    }

    /**
     * 获取用户的人脸列表
     */
    @GetMapping("/faces")
    public Map<String, Object> getFaceList(
            @RequestParam String userId,
            @RequestParam String groupId) throws IOException {
        return faceAttendanceService.getFaceList(userId, groupId);
    }
}