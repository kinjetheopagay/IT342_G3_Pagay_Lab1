package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.AttendanceResponseDTO;
import com.staffguard.staffguard.service.AttendanceService;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;

    public AttendanceController(AttendanceService attendanceService, JwtUtil jwtUtil) {
        this.attendanceService = attendanceService;
        this.jwtUtil = jwtUtil;
    }

    // Employee Time In
    @PostMapping("/time-in")
    public AttendanceResponseDTO timeIn(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.timeIn(email);
    }

    // Employee Time Out
    @PostMapping("/time-out")
    public AttendanceResponseDTO timeOut(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.timeOut(email);
    }

    // Employee views their attendance
    @GetMapping("/my")
    public List<AttendanceResponseDTO> getMyAttendance(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.getMyAttendance(email);
    }

    // Admin views all attendance
    @GetMapping("/all")
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}