package com.face.sign.course.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.course.bean.vo.TodayCourseStudentVo;
import com.face.sign.course.entity.CourseScheduleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface CourseScheduleMapper extends IBaseMapper<CourseScheduleEntity> {

    /**
     * 获取当前学生今天的课程信息
     * @param studentId 学生ID
     * @param semester 所在学期
     * @param currentWeek 所在周
     * @param currentDayOfWeek 所在星期
     * @return 课程信息列表
     */
    List<TodayCourseStudentVo> getTodayCourseStudent(@Param("studentId") Long studentId,
                                                     @Param("semester") Date semester,
                                                     @Param("currentWeek") Integer currentWeek,
                                                     @Param("currentDayOfWeek") Integer currentDayOfWeek,
                                                     @Param("classId") Long classId);
}