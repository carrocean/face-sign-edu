package com.face.sign.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.Period;
import com.face.sign.common.util.SnowflakeUtils;
import com.face.sign.common.util.WeekCalculator;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.course.bean.dto.CourseScheduleDto;
import com.face.sign.course.bean.dto.ExcelCourseDto;
import com.face.sign.course.entity.ClassEntity;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.mapper.ClassMapper;
import com.face.sign.course.mapper.CourseMapper;
import com.face.sign.course.mapper.CourseScheduleMapper;
import com.face.sign.course.service.ICourseService;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CourseServiceImpl extends BaseServiceImpl<CourseEntity, CourseMapper> implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Date getRecentSemester() {
        Date currentDate = new Date();
        return courseMapper.getRecentSemester(currentDate);
    }

    @Override
    public List<CourseScheduleDto> getNowCourseSchedule() {
        Date semester = getRecentSemester();
        Integer currentWeek = WeekCalculator.getCurrentWeek(semester);
        Integer currentDayOfWeek = WeekCalculator.getCurrentDayOfWeek();
        Integer period = Period.getCurrentPeriod().getPeriodName();
        return courseMapper.getNowCourseSchedules(semester, currentWeek, currentDayOfWeek, period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCourses(List<ExcelCourseDto> courseDtos) {
        for (ExcelCourseDto courseDto : courseDtos) {
            // 查询或创建班级
            ClassEntity classEntity = classMapper.selectOne(new QueryWrapper<ClassEntity>().eq("class_name", courseDto.getClassName()));
            if (classEntity == null) {
                throw new BizException("班级" + courseDto.getClassName() + "不存在");
            }

            // 查询或创建教师
            TeacherEntity teacherEntity = teacherMapper.selectOne(new QueryWrapper<TeacherEntity>().eq("teacher_number", courseDto.getTeacherNumber()));
            if (teacherEntity == null) {
                throw new BizException("教师" + courseDto.getTeacherName() + "不存在");
            }

            // 查询课程是否存在
            CourseEntity courseEntity = courseMapper.selectOne(new QueryWrapper<CourseEntity>().eq("course_name", courseDto.getCourseName()));
            Long courseId = null;
            if (courseEntity == null) {
                // 创建课程
                courseEntity = new CourseEntity();
                courseEntity.setCourseName(courseDto.getCourseName());
                courseEntity.setTeacherId(teacherEntity.getId());
                courseEntity.setClassId(classEntity.getId());
                courseEntity.setSemester(courseDto.getSemester());
                courseEntity.setStartWeek(courseDto.getStartWeek());
                courseEntity.setEndWeek(courseDto.getEndWeek());
                courseId = Long.valueOf(SnowflakeUtils.createId());
                courseEntity.setId(courseId);
                courseMapper.insert(courseEntity);
            } else {
                courseId = courseEntity.getId();
            }

            // 创建课程安排
            for (int i = courseEntity.getStartWeek(); i <= courseEntity.getEndWeek(); i++) {
                CourseScheduleEntity scheduleEntity = new CourseScheduleEntity();
                scheduleEntity.setCourseId(courseId);
                scheduleEntity.setWeekDay(courseDto.getWeekDay());
                scheduleEntity.setPeriod(courseDto.getPeriod());
                scheduleEntity.setTheWeek(i);
                scheduleEntity.setClassroom(courseDto.getClassroom());
                courseScheduleMapper.insert(scheduleEntity);
            }
        }
    }

    public List<ExcelCourseDto> exportCourses(Map<String, Object> conditions, String token) {
        QueryWrapper<CourseEntity> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        List<CourseEntity> courseEntities = courseMapper.selectList(queryWrapper);

        List<ExcelCourseDto> courseDtos = new ArrayList<>();
        for (CourseEntity courseEntity : courseEntities) {
            // 查询教师信息
            TeacherEntity teacherEntity = teacherMapper.selectById(courseEntity.getTeacherId());
            String teacherName = teacherEntity != null ? teacherEntity.getName() : "";
            String teacherNumber = teacherEntity != null ? teacherEntity.getTeacherNumber() : "";

            // 查询班级信息
            ClassEntity classEntity = classMapper.selectById(courseEntity.getClassId());
            String className = classEntity != null ? classEntity.getClassName() : "";

            // 查询课程安排信息
            List<CourseScheduleEntity> scheduleEntities = courseScheduleMapper.selectList(new QueryWrapper<CourseScheduleEntity>().eq("course_id", courseEntity.getId()));
            for (CourseScheduleEntity scheduleEntity : scheduleEntities) {
                ExcelCourseDto courseDto = new ExcelCourseDto();
                courseDto.setCourseName(courseEntity.getCourseName());
                courseDto.setTeacherName(teacherName);
                courseDto.setTeacherNumber(teacherNumber);
                courseDto.setClassName(className);
                courseDto.setSemester(courseEntity.getSemester());
                courseDto.setStartWeek(courseEntity.getStartWeek());
                courseDto.setEndWeek(courseEntity.getEndWeek());
                courseDto.setWeekDay(scheduleEntity.getWeekDay());
                courseDto.setPeriod(scheduleEntity.getPeriod());
                courseDto.setClassroom(scheduleEntity.getClassroom());
                courseDtos.add(courseDto);
            }
        }

        return courseDtos;
    }
}