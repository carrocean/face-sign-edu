package com.face.sign.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.BaseUtil;
import com.face.sign.common.util.WeekCalculator;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.course.bean.vo.TodayCourseStudentVo;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.entity.CourseScheduleEntity;
import com.face.sign.course.entity.vo.CourseScheduleVo;
import com.face.sign.course.mapper.CourseMapper;
import com.face.sign.course.mapper.CourseScheduleMapper;
import com.face.sign.course.service.ICourseScheduleService;
import com.face.sign.course.service.ICourseService;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.mapper.StudentMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CourseScheduleServiceImpl extends BaseServiceImpl<CourseScheduleEntity, CourseScheduleMapper> implements ICourseScheduleService {

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ICourseService courseService;


    @Override
    public void addCourseSchedule(CourseScheduleVo courseScheduleVo) {
        for(int i = courseScheduleVo.getStartWeek(); i <= courseScheduleVo.getEndWeek(); i++) {
            CourseScheduleEntity courseScheduleEntity = BaseUtil.voToEntity(courseScheduleVo, CourseScheduleEntity.class);
            courseScheduleEntity.setTheWeek(i);
            courseScheduleMapper.insert(courseScheduleEntity);
        }
    }

    @Override
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));
        Date semester = courseService.getRecentSemester();
        List<CourseEntity> courseEntities = courseMapper.selectList(new QueryWrapper<CourseEntity>().eq("class_id", student.getClassId()).eq("semester", semester));
        List<Long> courseIds = courseEntities.stream().map(CourseEntity::getId).toList();

        QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
        if(fuzzySearch){
            QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        } else {
            QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
        }
        if(!courseIds.isEmpty()){
            queryWrapper.in("course_id", courseIds);
        } else {
            queryWrapper.in("course_id", "");
        }

        queryWrapper.orderByDesc("add_time");

        if(pageSearch){
            return courseScheduleMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        } else {
            Page<CourseScheduleEntity> page = new Page<>(1, Integer.MAX_VALUE);
            return courseScheduleMapper.selectPage(page, queryWrapper);
        }
    }

    @Override
    public Object getTodayCourseStudent(String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);
        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));

        Date now = new Date();
        Date semester = courseService.getRecentSemester();
        Integer currentWeek = WeekCalculator.getCurrentWeek(semester);
        Integer currentDayOfWeek = WeekCalculator.getCurrentDayOfWeek();

        List<TodayCourseStudentVo> todayCourseStudentVos = courseScheduleMapper.getTodayCourseStudent(student.getId(), semester, currentWeek, currentDayOfWeek, student.getClassId());

        long[][] periods = new long[4][2];
        periods[0][0] = new Date(now.getYear(), now.getMonth(), now.getDate(), 8, 0).getTime();   // 第1节开始
        periods[0][1] = new Date(now.getYear(), now.getMonth(), now.getDate(), 9, 40).getTime(); // 第1节结束
        periods[1][0] = new Date(now.getYear(), now.getMonth(), now.getDate(), 10, 0).getTime(); // 第2节开始
        periods[1][1] = new Date(now.getYear(), now.getMonth(), now.getDate(), 11, 40).getTime(); // 第2节结束
        periods[2][0] = new Date(now.getYear(), now.getMonth(), now.getDate(), 14, 30).getTime(); // 第3节开始
        periods[2][1] = new Date(now.getYear(), now.getMonth(), now.getDate(), 16, 5).getTime();  // 第3节结束
        periods[3][0] = new Date(now.getYear(), now.getMonth(), now.getDate(), 16, 25).getTime(); // 第4节开始
        periods[3][1] = new Date(now.getYear(), now.getMonth(), now.getDate(), 18, 0).getTime();  // 第4节结束

        // 根据当前时间设置状态
        for (TodayCourseStudentVo course : todayCourseStudentVos) {
            long nowTime = now.getTime();
            int periodIndex = course.getPeriod() - 1; // 节次索引从0开始

            // 如果当前时间在开始时间前20分钟内
            long twentyMinutesBefore = periods[periodIndex][0] - 20 * 60 * 1000;
            if (nowTime >= twentyMinutesBefore && nowTime < periods[periodIndex][1]) {
                course.setStatus("ongoing");
            } else if (nowTime < twentyMinutesBefore) {
                course.setStatus("pending");
            } else {
                course.setStatus("ended");
            }
        }

        return todayCourseStudentVos;
    }
}