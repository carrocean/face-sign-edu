package com.face.sign.attendance.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.common.base.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

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
}