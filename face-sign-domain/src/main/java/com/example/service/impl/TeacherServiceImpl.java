package com.example.service.impl;

import com.example.entity.Teacher;
import com.example.mapper.TeacherMapper;
import com.example.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements ITeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Teacher addTeacher(Teacher teacher) {
        teacherMapper.addTeacher(teacher);
        return teacher;
    }

    @Override
    public Teacher getTeacherById(Long teacherId) {
        return teacherMapper.getTeacherById(teacherId);
    }

    @Override
    public Teacher getTeacherByTeacherNo(String teacherNo) {
        return teacherMapper.getTeacherByTeacherNo(teacherNo);
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        teacherMapper.updateTeacher(teacher);
        return teacher;
    }

    @Override
    public void deleteTeacher(Long teacherId) {
        teacherMapper.deleteTeacher(teacherId);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherMapper.getAllTeachers();
    }
}