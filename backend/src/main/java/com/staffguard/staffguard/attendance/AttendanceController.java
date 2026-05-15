package com.staffguard.staffguard.attendance;

import com.staffguard.staffguard.shared.util.JwtUtil;
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

    @PostMapping("/time-in")
    public AttendanceResponse timeIn(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.timeIn(email);
    }

    @PostMapping("/time-out")
    public AttendanceResponse timeOut(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.timeOut(email);
    }

    @GetMapping("/my")
    public List<AttendanceResponse> getMyAttendance(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return attendanceService.getMyAttendance(email);
    }

    @GetMapping("/all")
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}