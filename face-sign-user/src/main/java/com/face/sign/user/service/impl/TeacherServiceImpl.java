package com.face.sign.user.service.impl;

import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.TeacherMapper;
import com.face.sign.user.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements ITeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public TeacherEntity addTeacher(TeacherEntity teacher) {
        teacherMapper.addTeacher(teacher);
        return teacher;
    }

    @Override
    public TeacherEntity getTeacherById(Integer teacherId) {
        return teacherMapper.getTeacherById(teacherId);
    }

    @Override
    public TeacherEntity getTeacherByTeacherNo(String teacherNo) {
        return teacherMapper.getTeacherByTeacherNo(teacherNo);
    }

    @Override
    public TeacherEntity updateTeacher(TeacherEntity teacher) {
        teacherMapper.updateTeacher(teacher);
        return teacher;
    }

    @Override
    public void deleteTeacher(Integer teacherId) {
        teacherMapper.deleteTeacher(teacherId);
    }

    @Override
    public List<TeacherEntity> getAllTeachers() {
        return teacherMapper.getAllTeachers();
    }
}