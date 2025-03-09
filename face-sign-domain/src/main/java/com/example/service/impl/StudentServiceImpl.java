package com.example.service.impl;

import com.example.entity.Student;
import com.example.mapper.StudentMapper;
import com.example.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements IStudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Student addStudent(Student student) {
        studentMapper.addStudent(student);
        return student;
    }

    @Override
    public Student getStudentById(Long studentId) {
        return studentMapper.getStudentById(studentId);
    }

    @Override
    public Student getStudentByStudentNo(String studentNo) {
        return studentMapper.getStudentByStudentNo(studentNo);
    }

    @Override
    public Student updateStudent(Student student) {
        studentMapper.updateStudent(student);
        return student;
    }

    @Override
    public void deleteStudent(Long studentId) {
        studentMapper.deleteStudent(studentId);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.getAllStudents();
    }
}