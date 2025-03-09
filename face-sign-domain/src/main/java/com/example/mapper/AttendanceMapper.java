package com.example.mapper;

import com.example.entity.Attendance;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AttendanceMapper {
    @Select("SELECT * FROM attendance WHERE attendance_id = #{attendanceId}")
    Attendance getAttendanceById(@Param("attendanceId") Long attendanceId);

    @Insert("INSERT INTO attendance (student_id, course_id, attendance_date, status, created_at) " +
            "VALUES (#{studentId}, #{courseId}, #{attendanceDate}, #{status}, #{createdAt})")
    int addAttendance(Attendance attendance);

    @Update("UPDATE attendance SET status = #{status}, updated_at = #{updatedAt} " +
            "WHERE attendance_id = #{attendanceId}")
    int updateAttendance(Attendance attendance);

    @Delete("DELETE FROM attendance WHERE attendance_id = #{attendanceId}")
    int deleteAttendance(@Param("attendanceId") Long attendanceId);

    @Select("SELECT * FROM attendance WHERE student_id = #{studentId} AND course_id = #{courseId}")
    List<Attendance> getAttendanceByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                                         @Param("courseId") Long courseId);

    @Select("SELECT * FROM attendance")
    List<Attendance> getAllAttendance();
}