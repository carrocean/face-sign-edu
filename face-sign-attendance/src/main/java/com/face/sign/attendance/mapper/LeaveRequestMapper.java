package com.face.sign.attendance.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.common.base.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

@Mapper
public interface LeaveRequestMapper extends IBaseMapper<LeaveRequestEntity> {

    /**
     * 自定义分页查询方法
     * @param page
     * @param conditions
     * @return
     */
    IPage<Map<String, Object>> selectLeaveRequestsByConditions(Page<?> page, Map<String, Object> conditions);

    /**
     * 检查当前时间当前课程该学生是否请假
     * @param studentId 学生ID
     * @param teacherId 课程ID
     * @param now 当前时间
     * @return 是否请假
     */
    LeaveRequestEntity checkNowIsLeaveByStudent(@Param("studentId") Long studentId,
                                          @Param("teacherId") Long teacherId,
                                          @Param("now") Date now);
}