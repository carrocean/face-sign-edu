package com.example.gateway.controller;

import com.example.entity.Attendance;
import com.example.service.IAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private IAttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Attendance> addAttendance(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.addAttendance(attendance));
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long attendanceId) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(attendanceId));
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long attendanceId, @RequestBody Attendance attendance) {
        attendance.setAttendanceId(attendanceId);
        return ResponseEntity.ok(attendanceService.updateAttendance(attendance));
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudentIdAndCourseId(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentIdAndCourseId(studentId, courseId));
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }
}