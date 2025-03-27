package com.face.sign.attendance.mapper;

import com.face.sign.attendance.entity.AttendanceRecordEntity;
import com.face.sign.common.base.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤记录Mapper接口
 */
@Mapper
public interface AttendanceRecordMapper extends IBaseMapper<AttendanceRecordEntity> {}