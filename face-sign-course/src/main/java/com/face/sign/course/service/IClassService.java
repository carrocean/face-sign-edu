package com.face.sign.course.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.course.bean.dto.ExcelStudentDto;
import com.face.sign.course.entity.ClassEntity;

import java.util.List;
import java.util.Map;

public interface IClassService extends IBaseService<ClassEntity> {

    /**
     * 批量导入学生
     * @param students
     */
    void importStudents(List<ExcelStudentDto> students);

    /**
     * 获取学生列表
     * @param conditions
     * @param token
     * @return
     */
    List<ExcelStudentDto> queryStudents(Map<String, Object> conditions, String token);
}