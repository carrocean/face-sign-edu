package com.face.sign.attendance.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.bean.vo.AdminAttendanceRecordVo;
import com.face.sign.attendance.bean.vo.StudentAttendanceRecordVo;
import com.face.sign.attendance.bean.vo.StudentCountVo;
import com.face.sign.attendance.bean.vo.TeacherAttendanceRecordVo;
import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.common.base.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 考勤记录Mapper接口
 */
@Mapper
public interface AttendanceRecordMapper extends IBaseMapper<AttendanceRecordEntity> {

    /**
     * 条件分页查询考勤记录
     * @param page 分页参数
     * @return 分页结果
     */
    public IPage<StudentAttendanceRecordVo> selectStudentAttendanceRecordPage(Page<StudentAttendanceRecordVo> page, Map<String, Object> conditions);

    /**
     * 学生端统计信息
     * @param studentId 学生ID
     * @param todayStart 今日开始时间
     * @param weekStart 本周开始时间
     * @param currentWeek 当前周数
     * @param currentDayOfWeek 当前星期几
     * @return 统计信息
     */
    StudentCountVo selectStudentCountVo(@Param("studentId") Long studentId,
                                        @Param("todayStart") LocalDateTime todayStart,
                                        @Param("weekStart") LocalDateTime weekStart,
                                        @Param("currentWeek") int currentWeek,
                                        @Param("currentDayOfWeek") int currentDayOfWeek);

    /**
     * 管理员端条件分页查询记录
     * @param page
     * @param conditions
     * @return
     */
    IPage<AdminAttendanceRecordVo> selectAdminAttendanceRecordPage(Page<AdminAttendanceRecordVo> page, Map<String, Object> conditions);

    /**
     * 教师端条件分页查询记录
     * @param page
     * @param conditions
     * @return
     */
    IPage<TeacherAttendanceRecordVo> selectTeacherAttendanceRecordPage(Page<TeacherAttendanceRecordVo> page, Map<String, Object> conditions);
}