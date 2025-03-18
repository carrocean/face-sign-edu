package com.face.sign.user.service;

import com.face.sign.user.entity.StudentEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStudentService {
    // 添加学生信息（支持上传人脸图像）
    StudentEntity addStudent(StudentEntity student, MultipartFile faceImage) throws Exception;

    // 根据学生ID获取学生信息
    StudentEntity getStudentById(Integer studentId);

    // 根据学号获取学生信息
    StudentEntity getStudentByStudentNo(String studentNo);

    // 更新学生信息（支持更新人脸图像）
    StudentEntity updateStudent(Integer studentId, StudentEntity student, MultipartFile faceImage) throws Exception;

    // 删除学生信息
    void deleteStudent(Integer studentId);

    // 获取所有学生信息
    List<StudentEntity> getAllStudents();
}