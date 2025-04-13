package com.face.sign.course.service.impl;

import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.BaseUtil;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;
import com.face.sign.course.mapper.CourseScheduleMapper;
import com.face.sign.course.service.ICourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseScheduleServiceImpl extends BaseServiceImpl<CourseScheduleEntity, CourseScheduleMapper> implements ICourseScheduleService {

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Override
    public void addCourseSchedule(CourseScheduleVo courseScheduleVo) {
        for(int i = courseScheduleVo.getStartWeek(); i <= courseScheduleVo.getEndWeek(); i++) {
            CourseScheduleEntity courseScheduleEntity = BaseUtil.voToEntity(courseScheduleVo, CourseScheduleEntity.class);
            courseScheduleEntity.setTheWeek(i);
            courseScheduleMapper.insert(courseScheduleEntity);
        }
    }
}