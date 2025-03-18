package com.face.sign.user.service.impl;

import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StudentServiceImpl implements IStudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public StudentEntity addStudent(StudentEntity student, MultipartFile faceImage) throws Exception {
        // 上传人脸图像到 MinIO
        String faceImagePath = uploadFaceImage(faceImage);

        // 设置学生信息
        student.setFaceImagePath(faceImagePath);
        student.setCreatedAt(LocalDateTime.now());

        // 保存学生信息到数据库
        studentMapper.addStudent(student);
        return student;
    }

    @Override
    public StudentEntity getStudentById(Long studentId) {
        return studentMapper.getStudentById(studentId);
    }

    @Override
    public StudentEntity getStudentByStudentNo(String studentNo) {
        return studentMapper.getStudentByStudentNo(studentNo);
    }

    @Override
    public StudentEntity updateStudent(Long studentId, StudentEntity student, MultipartFile faceImage) throws Exception {
        // 获取现有学生信息
        StudentEntity existingStudent = studentMapper.getStudentById(studentId);

        // 如果上传了新的图像，则更新图像路径
        if (faceImage != null && !faceImage.isEmpty()) {
            // 删除旧图像（如果存在）
            if (existingStudent.getFaceImagePath() != null) {
                MinIOUtils.removeFile(bucketName, existingStudent.getFaceImagePath());
            }

            // 上传新图像
            String newImagePath = uploadFaceImage(faceImage);
            student.setFaceImagePath(newImagePath);
        }

        // 更新学生信息
        student.setStudentId(studentId);
        student.setUpdatedAt(LocalDateTime.now());
        studentMapper.updateStudent(student);

        return student;
    }

    @Override
    public void deleteStudent(Long studentId) {
        StudentEntity student = studentMapper.getStudentById(studentId);

        // 删除人脸图像（如果存在）
        if (student.getFaceImagePath() != null) {
            try {
                MinIOUtils.removeFile(bucketName, student.getFaceImagePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 删除学生信息
        studentMapper.deleteStudent(studentId);
    }

    @Override
    public List<StudentEntity> getAllStudents() {
        return studentMapper.getAllStudents();
    }

    // 辅助方法：上传人脸图像到 MinIO
    private String uploadFaceImage(MultipartFile faceImage) throws Exception {
        String objectName = UUID.randomUUID().toString() + faceImage.getOriginalFilename().substring(faceImage.getOriginalFilename().lastIndexOf("."));
        MinIOUtils.uploadFile(bucketName, faceImage, objectName, "image/jpeg");
        return objectName;
    }
}