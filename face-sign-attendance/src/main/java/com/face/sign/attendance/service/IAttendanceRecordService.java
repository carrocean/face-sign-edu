package com.face.sign.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.face.sign.attendance.bean.dto.AttendanceRecordDTO;
import com.face.sign.attendance.bean.vo.StudentCountVo;
import com.face.sign.attendance.bean.vo.TeacherAttendanceRecordVo;
import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.common.base.IBaseService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 考勤记录服务接口
 */
public interface IAttendanceRecordService extends IBaseService<AttendanceRecordEntity> {
    
    /**
     * 人脸识别签到
     * @return 签到结果
     */
    Boolean signIn(MultipartFile image, Long scheduleId, Long courseId, String token);

    /**
     * 上传照片
     * @param userId
     * @param image
     */
    void uploadImage(String userId, MultipartFile image);

    /**
     * 预览照片
     * @param userId
     * @return
     */
    String preview(String userId);

    /**
     * 根据老师获取考勤分页列表
     *
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param conditions
     * @return
     */
    public IPage<TeacherAttendanceRecordVo> pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions, String token);

    /**
     *
     * 根据学生获取考勤分页列表
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param searchForm
     * @return
     */
    public Object pageByStudent(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> searchForm, String token);

    /**
     * 教师查询需要导出的考勤记录
     * @param conditions
     * @param token
     * @return
     */
    public List<AttendanceRecordDTO> queryTeacherAttendanceRecords(Map<String, Object> conditions, String token);

    /**
     * 管理员查询需要导出的考勤记录
     * @param conditions
     * @param token
     * @return
     */
    public List<AttendanceRecordDTO> queryAdminAttendanceRecords(Map<String, Object> conditions, String token);

    /**
     * 学生端统计信息
     * @param token
     * @return
     */
    public StudentCountVo getStudentCount(String token);

    /**
     * 处理未考勤和请假学生的考勤记录
     */
    public void handleAttendanceRecords();
}