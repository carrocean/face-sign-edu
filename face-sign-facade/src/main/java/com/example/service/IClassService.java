package com.example.service;

import com.example.entity.Class;

import java.util.List;

public interface IClassService {
    // 添加班级信息
    Class addClass(Class classEntity);

    // 根据班级ID获取班级信息
    Class getClassById(Long classId);

    // 更新班级信息
    Class updateClass(Class classEntity);

    // 删除班级信息
    void deleteClass(Long classId);

    // 获取所有班级信息
    List<Class> getAllClasses();
}