package com.staffguard.staffguard.schedule;

import com.staffguard.staffguard.shared.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtUtil jwtUtil;

    public ScheduleController(ScheduleService scheduleService, JwtUtil jwtUtil) {
        this.scheduleService = scheduleService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ScheduleResponse createSchedule(@RequestBody ScheduleRequest request) {
        return scheduleService.createSchedule(request);
    }

    @GetMapping("/all")
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/date/{date}")
    public List<ScheduleResponse> getByDate(@PathVariable String date) {
        return scheduleService.getSchedulesByDate(date);
    }

    @GetMapping("/my/today")
    public Optional<ScheduleResponse> getMyTodaySchedule(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return scheduleService.getMyTodaySchedule(email);
    }

@GetMapping("/my")
    public List<ScheduleResponse> getMySchedules(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return scheduleService.getMySchedules(email);
    }

    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        return scheduleService.deleteSchedule(id);
    }
}