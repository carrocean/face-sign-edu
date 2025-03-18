package com.face.sign.service.impl;

import com.face.sign.entity.Class;
import com.face.sign.mapper.ClassMapper;
import com.face.sign.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements IClassService {
    @Autowired
    private ClassMapper classMapper;

    @Override
    public Class addClass(Class classEntity) {
        classMapper.addClass(classEntity);
        return classEntity;
    }

    @Override
    public Class getClassById(Long classId) {
        return classMapper.getClassById(classId);
    }

    @Override
    public Class updateClass(Class classEntity) {
        classMapper.updateClass(classEntity);
        return classEntity;
    }

    @Override
    public void deleteClass(Long classId) {
        classMapper.deleteClass(classId);
    }

    @Override
    public List<Class> getAllClasses() {
        return classMapper.getAllClasses();
    }
}