package com.face.sign.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.attendance.mapper.LeaveRequestMapper;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.common.util.mybatisplus.QueryWrapperUtils;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.mapper.CourseMapper;
import com.face.sign.course.service.ICourseService;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.mapper.TeacherMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LeaveRequestServiceImpl extends BaseServiceImpl<LeaveRequestEntity, LeaveRequestMapper> implements ILeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ICourseService courseService;
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Object page(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions) {
        if (pageSearch) {
            return leaveRequestMapper.selectLeaveRequestsByConditions(new Page<>(currentPage, pageSize), conditions);
        } else {
            return leaveRequestMapper.selectLeaveRequestsByConditions(new Page<>(1, Integer.MAX_VALUE), conditions);
        }
    }

    @Override
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));

        QueryWrapper<LeaveRequestEntity> queryWrapper = new QueryWrapper<>();
        if(fuzzySearch){
            QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        } else {
            QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
        }
        queryWrapper.eq("student_id", student.getId());
        queryWrapper.orderByDesc("add_time");

        if(pageSearch){
            return leaveRequestMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        } else {
            Page<LeaveRequestEntity> page = new Page<>(1, Integer.MAX_VALUE);
            return leaveRequestMapper.selectPage(page, queryWrapper);
        }
    }

    @Override
    public Object pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        TeacherEntity teacher = teacherMapper.selectOne(new QueryWrapper<TeacherEntity>().eq("user_id", userId));

        QueryWrapper<LeaveRequestEntity> queryWrapper = new QueryWrapper<>();
        if(fuzzySearch){
            QueryWrapperUtils.buildLikeQueryWrapper(queryWrapper, conditions);
        } else {
            QueryWrapperUtils.buildQueryWrapper(queryWrapper, conditions);
        }
        queryWrapper.eq("approver_id", teacher.getId());
        queryWrapper.orderByDesc("add_time");

        if(pageSearch){
            return leaveRequestMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        } else {
            Page<LeaveRequestEntity> page = new Page<>(1, Integer.MAX_VALUE);
            return leaveRequestMapper.selectPage(page, queryWrapper);
        }
    }

    @Override
    public void saveByStudent(LeaveRequestEntity entity, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));
        Date semester = courseService.getRecentSemester();
        List<CourseEntity> courseEntities = courseMapper.selectList(new QueryWrapper<CourseEntity>()
                .eq("class_id", student.getClassId())
                .eq("semester", semester));

        List<Long> teacherIds = courseEntities.stream().map(CourseEntity::getTeacherId).toList();

        entity.setStudentId(student.getId());
        entity.setStatus("pending");

        for(Long teacherId : teacherIds){
            entity.setApproverId(teacherId);
            leaveRequestMapper.insert(entity);
        }
    }

    @Override
    public Boolean checkNowIsLeaveByStudent(Long studentId, Long courseId) {
        CourseEntity course = courseMapper.selectOne(new QueryWrapper<CourseEntity>().eq("id", courseId));
        LeaveRequestEntity leaveRequest = leaveRequestMapper.checkNowIsLeaveByStudent(studentId, course.getTeacherId(), new Date());
        return leaveRequest != null;
    }

}