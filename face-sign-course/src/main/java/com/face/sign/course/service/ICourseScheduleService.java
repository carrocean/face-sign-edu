package com.face.sign.course.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;

import java.util.Map;

public interface ICourseScheduleService extends IBaseService<CourseScheduleEntity> {
    void addCourseSchedule(CourseScheduleVo courseScheduleVo);

    /**
     *
     * 根据学生获取考勤分页列表
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param searchForm
     * @return
     */
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> searchForm, String token);

    /**
     * 获取今日课程
     * @param token
     * @return
     */
    Object getTodayCourseStudent(String token);
}