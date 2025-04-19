package com.face.sign.attendance.service;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.common.base.IBaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 考勤记录服务接口
 */
public interface IAttendanceRecordService extends IBaseService<AttendanceRecordEntity> {
    
    /**
     * 人脸识别签到
     * @return 签到结果
     */
    Boolean signIn(Long scheduleId, MultipartFile image);

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
}