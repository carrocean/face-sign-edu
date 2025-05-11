package com.face.sign.attendance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.bean.dto.AttendanceRecordDTO;
import com.face.sign.attendance.bean.vo.AdminAttendanceRecordVo;
import com.face.sign.attendance.bean.vo.StudentAttendanceRecordVo;
import com.face.sign.attendance.bean.vo.StudentCountVo;
import com.face.sign.attendance.bean.vo.TeacherAttendanceRecordVo;
import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.mapper.AttendanceRecordMapper;
import com.face.sign.attendance.mapper.LeaveRequestMapper;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.Period;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.common.util.jwt.JwtUtils;
import com.face.sign.common.util.minio.MinioUtil;
import com.face.sign.course.bean.dto.CourseScheduleDto;
import com.face.sign.course.entity.CourseEntity;
import com.face.sign.course.mapper.ClassMapper;
import com.face.sign.course.mapper.CourseMapper;
import com.face.sign.course.mapper.CourseScheduleMapper;
import com.face.sign.course.service.ICourseService;
import com.face.sign.recognition.util.BaiduFaceUtil;
import com.face.sign.user.entity.StudentEntity;
import com.face.sign.user.entity.TeacherEntity;
import com.face.sign.user.mapper.StudentMapper;
import com.face.sign.user.mapper.TeacherMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

import static com.face.sign.common.util.Period.getCurrentPeriod;

/**
 * 考勤记录服务实现类
 */
@Service
public class AttendanceRecordServiceImpl extends BaseServiceImpl<AttendanceRecordEntity, AttendanceRecordMapper> implements IAttendanceRecordService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaiduFaceUtil baiduFaceUtil;

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private ILeaveRequestService leaveRequestService;


    @Override
    public Object page(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions) {
        Page<AdminAttendanceRecordVo> page = new Page<>(currentPage, pageSize);
        return attendanceRecordMapper.selectAdminAttendanceRecordPage(page, conditions);
    }


    @Override
    public Boolean signIn(MultipartFile image, Long scheduleId, Long courseId, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);
        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));

        AttendanceRecordEntity attendanceRecordEntity = attendanceRecordMapper.selectOne(new QueryWrapper<AttendanceRecordEntity>().eq("student_id", student.getId()).eq("schedule_id", scheduleId).eq("course_id", courseId));
        if(attendanceRecordEntity != null){
            throw new BizException("已考勤");
        }

        try {
            // 1. 验证人脸
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());

            JSONObject searchResult = baiduFaceUtil.searchFace(imageBase64, String.valueOf(student.getClassId()));
            if (searchResult.getInt("error_code") != 0) {
                throw new BizException(searchResult.getString("error_msg"));
            }

            // 检查搜索结果
            JSONObject result = searchResult.getJSONObject("result");
            JSONArray userList = result.getJSONArray("user_list");
            if (userList.length() == 0) {
                throw new BizException("未找到匹配结果");
            }

            // 检查最佳匹配结果
            JSONObject bestMatch = userList.getJSONObject(0);
            if (!bestMatch.getString("user_id").equals(student.getStudentNumber())) {
                throw new BizException("识别用户id与当前用户不匹配");
            }

            // 检查相似度是否达到阈值
            double similarity = bestMatch.getDouble("score");
            if (similarity < 80) {
                throw new BizException("人脸识别相似度过低，验证失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        String status = Period.getAttendanceStatus();

        // 更新考勤记录
        QueryWrapper<AttendanceRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schedule_id", scheduleId);
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("student_id", student.getId());
        AttendanceRecordEntity record = attendanceRecordMapper.selectOne(queryWrapper);

        if (record == null) {
            record = new AttendanceRecordEntity();
            record.setScheduleId(scheduleId);
            record.setCourseId(courseId);
            record.setStudentId(student.getId());
            record.setStatus(status);
            record.setAttendanceDate(new Date());
            attendanceRecordMapper.insert(record);
        } else {
            record.setStatus(status);
            record.setAttendanceDate(new Date());
            record.setUpdateTime(new Date());
            attendanceRecordMapper.updateById(record);
        }

        return true;
    }

    @Override
    public void uploadImage(String userId, MultipartFile image) {
        QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        StudentEntity student = studentMapper.selectOne(queryWrapper);

        String imageBase64 = "";
        try {
            imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        JSONObject registerResult = baiduFaceUtil.registerFace(student.getStudentNumber(), imageBase64, student.getClassId().toString());
        if (registerResult.getInt("error_code") != 0) {
            throw new BizException(registerResult.getString("error_msg"));
        }

        minioUtil.upload(image, student.getClassId().toString(), student.getStudentNumber());

        String faceImage = student.getClassId().toString() + "/" + student.getStudentNumber();
        student.setFaceImage(faceImage);
        studentMapper.updateById(student);

    }

    @Override
    public String preview(String userId) {
        QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        StudentEntity student = studentMapper.selectOne(queryWrapper);

        String url = minioUtil.preview(student.getStudentNumber(), student.getClassId().toString());

        return url;
    }

    @Override
    public IPage<TeacherAttendanceRecordVo> pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        TeacherEntity teacher = teacherMapper.selectOne(new QueryWrapper<TeacherEntity>().eq("user_id", userId));
        List<CourseEntity> courseEntities = courseMapper.selectList(new QueryWrapper<CourseEntity>().eq("teacher_id", teacher.getId()));
        List<Long> courseIds = courseEntities.stream().map(CourseEntity::getId).toList();

        conditions.put("courseIds", courseIds);
        Page<TeacherAttendanceRecordVo> page = new Page<>(currentPage, pageSize);
        return attendanceRecordMapper.selectTeacherAttendanceRecordPage(page, conditions);
    }

    @Override
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);

        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));

        conditions.put("studentId", student.getId());
        Page<StudentAttendanceRecordVo> page = new Page<>(currentPage, pageSize);
        return attendanceRecordMapper.selectStudentAttendanceRecordPage(page, conditions);
    }

    @Override
    public List<AttendanceRecordDTO> queryTeacherAttendanceRecords(Map<String, Object> conditions, String token) {
        IPage<TeacherAttendanceRecordVo> teacherAttendanceRecordVos = pageByTeacher(1, Integer.MAX_VALUE, false, false, conditions, token);
        List<TeacherAttendanceRecordVo> teacherAttendanceRecordVoList = teacherAttendanceRecordVos.getRecords();

        List<AttendanceRecordDTO> recordDTOs = BeanUtil.copyToList(teacherAttendanceRecordVoList, AttendanceRecordDTO.class);

        return recordDTOs;
    }

    @Override
    public List<AttendanceRecordDTO> queryAdminAttendanceRecords(Map<String, Object> conditions, String token) {
        IPage<AdminAttendanceRecordVo> adminAttendanceRecordVos = (IPage<AdminAttendanceRecordVo>)page(1, Integer.MAX_VALUE, false, false, conditions);
        List<AdminAttendanceRecordVo> adminAttendanceRecordVoList = adminAttendanceRecordVos.getRecords();

        List<AttendanceRecordDTO> recordDTOs = BeanUtil.copyToList(adminAttendanceRecordVoList, AttendanceRecordDTO.class);

        return recordDTOs;
    }

    @Override
    public StudentCountVo getStudentCount(String token) {
        Jws<Claims> claims = JwtUtils.veifyJwtToken(token);
        String userId = claims.getPayload().get("id", String.class);
        StudentEntity student = studentMapper.selectOne(new QueryWrapper<StudentEntity>().eq("user_id", userId));

        // 获取当前日期的开始时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 获取本周的开始时间
        LocalDateTime weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();

        // 获取当前周数和星期几
        int currentWeek = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();

        return attendanceRecordMapper.selectStudentCountVo(student.getId(), todayStart, weekStart, currentWeek, currentDayOfWeek);
    }

    @Override
    public void handleAttendanceRecords() {
        Period currentPeriod = getCurrentPeriod();

        if (currentPeriod != null) {
            // 查询当前时间段的课程
            List<CourseScheduleDto> courseSchedules = courseService.getNowCourseSchedule();

            for (CourseScheduleDto courseSchedule : courseSchedules) {
                // 查询课程对应的班级
                Long classId = courseSchedule.getClassId();

                // 查询班级下的所有学生
                List<StudentEntity> students = studentMapper.selectList(new QueryWrapper<StudentEntity>().eq("class_id", classId));

                for (StudentEntity student : students) {
                    // 查询学生是否已有考勤记录

                    AttendanceRecordEntity attendanceRecord = attendanceRecordMapper.selectOne(new QueryWrapper<AttendanceRecordEntity>()
                            .eq("student_id", student.getId())
                            .eq("course_id", courseSchedule.getCourseId())
                            .eq("schedule_id", courseSchedule.getScheduleId()));

                    if (attendanceRecord == null) {
                        // 没有考勤记录，检查请假表
                        Boolean isLeave = leaveRequestService.checkNowIsLeaveByStudent(student.getId(), courseSchedule.getCourseId());

                        if (isLeave) {
                            // 有请假记录，插入考勤记录并标记为请假
                            attendanceRecord = new AttendanceRecordEntity();
                            attendanceRecord.setStudentId(student.getId());
                            attendanceRecord.setScheduleId(courseSchedule.getScheduleId());
                            attendanceRecord.setCourseId(courseSchedule.getCourseId());
                            attendanceRecord.setStatus("LEAVE"); // 请假
                            attendanceRecord.setAttendanceDate(new Date());
                            attendanceRecordMapper.insert(attendanceRecord);
                        } else {
                            // 没有请假记录，插入考勤记录并标记为缺勤
                            attendanceRecord = new AttendanceRecordEntity();
                            attendanceRecord.setStudentId(student.getId());
                            attendanceRecord.setScheduleId(courseSchedule.getScheduleId());
                            attendanceRecord.setCourseId(courseSchedule.getCourseId());
                            attendanceRecord.setStatus("ABSENT"); // 缺勤
                            attendanceRecord.setAttendanceDate(new Date());
                            attendanceRecordMapper.insert(attendanceRecord);
                        }
                    }
                }
            }
        }
    }
}