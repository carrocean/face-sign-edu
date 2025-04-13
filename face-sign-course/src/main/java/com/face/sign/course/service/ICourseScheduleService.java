package com.face.sign.course.service;

import com.face.sign.common.base.IBaseService;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;

public interface ICourseScheduleService extends IBaseService<CourseScheduleEntity> {
    void addCourseSchedule(CourseScheduleVo courseScheduleVo);
}