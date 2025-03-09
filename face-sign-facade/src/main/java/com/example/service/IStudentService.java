package com.example.service;

import com.example.entity.Student;

import java.util.List;

public interface IStudentService {
    // 添加学生信息
    Student addStudent(Student student);

    // 根据学生ID获取学生信息
    Student getStudentById(Long studentId);

    // 根据学号获取学生信息
    Student getStudentByStudentNo(String studentNo);

    // 更新学生信息
    Student updateStudent(Student student);

    // 删除学生信息
    void deleteStudent(Long studentId);

    // 获取所有学生信息
    List<Student> getAllStudents();
}