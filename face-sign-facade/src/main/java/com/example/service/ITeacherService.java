package com.example.service;

import com.example.entity.Teacher;

import java.util.List;

public interface ITeacherService {
    // 添加教师信息
    Teacher addTeacher(Teacher teacher);

    // 根据教师ID获取教师信息
    Teacher getTeacherById(Long teacherId);

    // 根据教师工号获取教师信息
    Teacher getTeacherByTeacherNo(String teacherNo);

    // 更新教师信息
    Teacher updateTeacher(Teacher teacher);

    // 删除教师信息
    void deleteTeacher(Long teacherId);

    // 获取所有教师信息
    List<Teacher> getAllTeachers();
}