package com.face.sign.course.mapper;

import com.face.sign.common.base.IBaseMapper;
import com.face.sign.course.bean.dto.CourseScheduleDto;
import com.face.sign.course.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface CourseMapper extends IBaseMapper<CourseEntity> {

    /**
     * 查询早于当前时间的最近一个semester
     * @param currentDate 当前时间
     * @return 最近的semester
     */
    Date getRecentSemester(@Param("currentDate") Date currentDate);

    /**
     * 查询上一个上课时间段的课程信息
     * @param semester 学期开始时间
     * @param theWeek 所在周数
     * @param weekDay 星期
     * @param period 节次
     * @return 课程安排信息列表
     */
    List<CourseScheduleDto> getNowCourseSchedules(@Param("semester") Date semester,
                                                  @Param("theWeek") Integer theWeek,
                                                  @Param("weekDay") Integer weekDay,
                                                  @Param("period") Integer period);
}