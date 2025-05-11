package com.face.sign.user.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.user.bean.dto.ExcelTeacherDto;
import com.face.sign.user.entity.TeacherEntity;

import java.util.List;
import java.util.Map;

public interface ITeacherService extends IBaseService<TeacherEntity> {

    /**
     * 根据用户ID获取用户详情
     * @param id
     * @return
     */
    TeacherEntity getByUserId(Long id);

    /**
     * 新增教师
     * @param teacher
     */
    void addTeacher(TeacherEntity teacher);

    /**
     * 批量导入教师
     * @param teacherDtos 教师数据列表
     */
    void importTeachers(List<ExcelTeacherDto> teacherDtos);

    /**
     * 查询教师数据
     * @param conditions 查询条件
     * @param token 鉴权令牌
     * @return 教师数据列表
     */
    List<ExcelTeacherDto> queryTeachers(Map<String, Object> conditions, String token);
}