package com.face.sign.attendance.service;

import com.face.sign.attendance.entity.LeaveRequestEntity;
import com.face.sign.common.base.IBaseService;

import java.util.Map;

public interface ILeaveRequestService extends IBaseService<LeaveRequestEntity> {

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
     * 根据学生添加请假申请
     * @param entity
     */
    void saveByStudent(LeaveRequestEntity entity, String token);

    /**
     * 检查指定学生当前课程是否请假
     *
     * @param studentId
     * @param courseId
     * @return
     */
    Boolean checkNowIsLeaveByStudent(Long studentId, Long courseId);

    /**
     * 教师查询请假列表
     * @param currentPage
     * @param pageSize
     * @param pageSearch
     * @param fuzzySearch
     * @param searchForm
     * @param token
     * @return
     */
    Object pageByTeacher(int currentPage, int pageSize, boolean pageSearch, boolean fuzzySearch, Map<String, Object> searchForm, String token);
}