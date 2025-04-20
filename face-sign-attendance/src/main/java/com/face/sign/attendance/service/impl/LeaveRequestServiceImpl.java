package com.face.sign.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.attendance.mapper.LeaveRequestMapper;
import com.face.sign.attendance.service.ILeaveRequestService;
import com.face.sign.common.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LeaveRequestServiceImpl extends BaseServiceImpl<LeaveRequestEntity, LeaveRequestMapper> implements ILeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Override
    public Object page(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> conditions) {
        if (pageSearch) {
            return leaveRequestMapper.selectLeaveRequestsByConditions(new Page<>(currentPage, pageSize), conditions);
        } else {
            return leaveRequestMapper.selectLeaveRequestsByConditions(new Page<>(1, Integer.MAX_VALUE), conditions);
        }
    }

}