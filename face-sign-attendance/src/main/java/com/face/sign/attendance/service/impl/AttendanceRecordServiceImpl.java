package com.face.sign.attendance.service.impl;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.mapper.AttendanceRecordMapper;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.recognition.util.BaiduFaceUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

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


    @Override
    public Boolean signIn(Long scheduleId, MultipartFile image) {
        try {
            // 1. 验证人脸
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());

            JSONObject searchResult = baiduFaceUtil.searchFace(imageBase64, "group001");
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
            if (!bestMatch.getString("user_id").equals(String.valueOf(getCurrentStudentNumber()))) {
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

        // 2. 创建考勤记录
        AttendanceRecordEntity record = new AttendanceRecordEntity();
        record.setCourseId(scheduleId);
        record.setStudentId(getCurrentStudentNumber());
        record.setStatus("PRESENT");
        record.setCheckInTime(LocalDateTime.now());
        attendanceRecordMapper.insert(record);

        return true;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentStudentNumber() {
        // TODO: 从SecurityContext获取当前用户ID
        return 20250001L;
    }
}