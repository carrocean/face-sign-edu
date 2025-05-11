package com.face.sign.course.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.course.bean.dto.CourseScheduleDto;
import com.face.sign.course.bean.dto.ExcelCourseDto;
import com.face.sign.course.entity.CourseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICourseService extends IBaseService<CourseEntity> {

    /**
     * 获取早于当前时间的最近一个学期开始时间
     * @return 最近的semester
     */
    public Date getRecentSemester();

    /**
     * 获取上个时间段的课程信息
     * @return
     */
    public List<CourseScheduleDto> getNowCourseSchedule();

    /**
     * 批量导入课程
     * @param courses
     */
    void importCourses(List<ExcelCourseDto> courses);

    /**
     * 导出课程
     * @param conditions
     * @param token
     * @return
     */
    List<ExcelCourseDto> exportCourses(Map<String, Object> conditions, String token);
}