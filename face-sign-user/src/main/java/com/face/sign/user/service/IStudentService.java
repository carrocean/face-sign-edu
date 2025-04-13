package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.entity.StudentEntity;

public interface IStudentService extends IBaseService<StudentEntity> {

    /**
     * 根据用户ID获取用户详情
     * @param id
     * @return
     */
    StudentEntity getByUserId(Long id);

    /**
     * 新增学生
     * @param student
     */
    void addStudent(StudentEntity student);
}