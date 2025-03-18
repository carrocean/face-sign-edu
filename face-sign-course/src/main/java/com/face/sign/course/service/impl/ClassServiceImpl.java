package com.face.sign.course.service.impl;

import com.face.sign.common.base.IBaseServiceImpl;
import com.face.sign.course.entity.ClassEntity;
import com.face.sign.course.mapper.ClassMapper;
import com.face.sign.course.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassServiceImpl extends IBaseServiceImpl<ClassEntity, ClassMapper> implements IClassService {

    private ClassMapper classMapper;

    @Autowired
    public void setClassMapper(ClassMapper classMapper) {
        this.classMapper = classMapper;
        init(classMapper);
    }
}