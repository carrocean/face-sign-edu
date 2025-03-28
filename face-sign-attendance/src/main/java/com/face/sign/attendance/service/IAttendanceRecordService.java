package com.face.sign.attendance.service;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.common.base.IBaseService;
import com.face.sign.common.util.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 考勤记录服务接口
 */
public interface IAttendanceRecordService extends IBaseService<AttendanceRecordEntity> {
    
    /**
     * 获取考勤记录列表
     */
    PageResult getAttendanceList(Long courseId, String startDate, String endDate, Integer pageNum, Integer pageSize);
    
    /**
     * 获取今日待签到课程
     */
    List<AttendanceRecordEntity> getTodayPendingCourses();
    
    /**
     * 人脸识别签到
     * @return 签到结果
     */
    Boolean signIn(Long scheduleId, MultipartFile image);
    
    /**
     * 获取考勤详情
     */
    AttendanceRecordEntity getAttendanceDetail(Long id);
    
    /**
     * 导出考勤记录
     */
    void exportAttendance(Long id, HttpServletResponse response);
}