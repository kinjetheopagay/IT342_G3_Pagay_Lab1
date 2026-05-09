package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.ScheduleRequestDTO;
import com.staffguard.staffguard.dto.ScheduleResponseDTO;
import com.staffguard.staffguard.service.ScheduleService;
import com.staffguard.staffguard.util.JwtUtil;
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

    // Admin creates schedule
    @PostMapping
    public ScheduleResponseDTO createSchedule(@RequestBody ScheduleRequestDTO dto) {
        return scheduleService.createSchedule(dto);
    }

    // Admin gets all schedules
    @GetMapping("/all")
    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    // Get schedules by date
    @GetMapping("/date/{date}")
    public List<ScheduleResponseDTO> getByDate(@PathVariable String date) {
        return scheduleService.getSchedulesByDate(date);
    }

    // Employee gets their today's schedule
    @GetMapping("/my/today")
    public Optional<ScheduleResponseDTO> getMyTodaySchedule(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return scheduleService.getMyTodaySchedule(email);
    }

    // Admin deletes schedule
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        return scheduleService.deleteSchedule(id);
    }
}