package com.face.sign.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.attendance.mapper.AttendanceRecordMapper;
import com.face.sign.attendance.service.IAttendanceRecordService;
import com.face.sign.common.base.BaseServiceImpl;
import com.face.sign.common.util.PageResult;
import com.face.sign.common.util.exception.BizException;
import com.face.sign.recognition.util.BaiduFaceUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * 考勤记录服务实现类
 */
@Service
public class AttendanceRecordServiceImpl extends BaseServiceImpl<AttendanceRecordEntity, AttendanceRecordMapper> implements IAttendanceRecordService {

    @Autowired
    private BaiduFaceUtil baiduFaceUtil;

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;



    @Override
    public PageResult getAttendanceList(Long courseId, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<AttendanceRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(AttendanceRecordEntity::getCourseId, courseId);
        }
        if (startDate != null) {
            wrapper.ge(AttendanceRecordEntity::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(AttendanceRecordEntity::getDate, endDate);
        }
        wrapper.orderByDesc(AttendanceRecordEntity::getAddTime);

        // 分页查询
        Page<AttendanceRecordEntity> page = new Page<>(pageNum, pageSize);
        Page<AttendanceRecordEntity> result = attendanceRecordMapper.selectPage(page, wrapper); // 调用 MyBatis-Plus 的分页查询方法

        // 转换为PageResult
//        return new PageResult(result.getRecords(), result.getTotal());
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean signIn(Long scheduleId, MultipartFile image) {
        // 1. 验证人脸
        Boolean verifyResult = verifyFaceData(image, getCurrentStudentNumber());
        if (!verifyResult) {
            throw new BizException("");
        }

        // 2. 创建考勤记录
//        AttendanceRecordEntity record = new AttendanceRecordEntity();
//        record.setCourseId(scheduleId);
//        record.setStudentId(getCurrentStudentNumber());
//        record.setStatus("PRESENT");
//        record.setCheckInTime(LocalDateTime.now());
//        save(record); // 报错

        return true;
    }

    public Boolean verifyFaceData(MultipartFile image, Long studentId) {
        try {
            // 将图片转换为Base64
            String imageBase64 = Base64.getEncoder().encodeToString(image.getBytes());
//            String image = imageBase64.substring(imageBase64.indexOf(",") + 1, imageBase64.length());

            // 调用百度API进行人脸搜索
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
            if (!bestMatch.getString("user_id").equals(String.valueOf(studentId))) {
                throw new BizException("识别用户id与当前用户不匹配");
            }

            // 检查相似度是否达到阈值
            double similarity = bestMatch.getDouble("score");
            if (similarity < 80) {
                throw new BizException("人脸识别相似度过低，验证失败");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public AttendanceRecordEntity getAttendanceDetail(Long id) {
        return getById(id);
    }

    @Override
    public void exportAttendance(Long id, HttpServletResponse response) {
        try {
            // 1. 获取考勤记录
            AttendanceRecordEntity record = getById(id);
            if (record == null) {
                throw new RuntimeException("考勤记录不存在");
            }

            // 2. 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("考勤记录");

            // 3. 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "课程", "日期", "时间", "状态", "签到时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 4. 写入数据
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(record.getStudentId());
            dataRow.createCell(1).setCellValue(record.getStudentName());
            dataRow.createCell(2).setCellValue(record.getCourseName());
            dataRow.createCell(3).setCellValue(record.getDate());
            dataRow.createCell(4).setCellValue(record.getTime());
            dataRow.createCell(5).setCellValue(record.getStatus());
            dataRow.createCell(6).setCellValue(record.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 5. 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=attendance.xlsx");

            // 6. 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("导出考勤记录失败", e);
        }
    }

    @Override
    public List<AttendanceRecordEntity> getTodayPendingCourses() {
        // 获取今天的日期
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 构建查询条件
        LambdaQueryWrapper<AttendanceRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRecordEntity::getDate, today)
               .eq(AttendanceRecordEntity::getStatus, "PENDING")
               .orderByAsc(AttendanceRecordEntity::getTime);
        
        // 查询今日待签到课程
//        return attendanceRecordMapper.selectList(wrapper);
        return null;
    }


    /**
     * 获取当前用户ID
     */
    private Long getCurrentStudentNumber() {
        // TODO: 从SecurityContext获取当前用户ID
        return 20250001L;
    }
}