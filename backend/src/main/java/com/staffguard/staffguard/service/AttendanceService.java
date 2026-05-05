package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.AttendanceResponseDTO;
import com.staffguard.staffguard.exception.UserNotFoundException;
import com.staffguard.staffguard.model.Attendance;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.AttendanceRepository;
import com.staffguard.staffguard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                              UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    // Employee Time In
    public AttendanceResponseDTO timeIn(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        // Check if already timed in today
        Optional<Attendance> existing = attendanceRepository
                .findByEmployeeAndDate(employee, today);

        if (existing.isPresent()) {
            throw new RuntimeException("Already timed in today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setTimeIn(LocalTime.now());
        attendance.setStatus("PRESENT");

        return toDTO(attendanceRepository.save(attendance));
    }

    // Employee Time Out
    public AttendanceResponseDTO timeOut(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, today)
                .orElseThrow(() -> new RuntimeException("No time-in record found for today"));

        if (attendance.getTimeOut() != null) {
            throw new RuntimeException("Already timed out today");
        }

        attendance.setTimeOut(LocalTime.now());
        return toDTO(attendanceRepository.save(attendance));
    }

    // Employee views their own attendance
    public List<AttendanceResponseDTO> getMyAttendance(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return attendanceRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Admin views all attendance
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAllByOrderByDateDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AttendanceResponseDTO toDTO(Attendance a) {
        return new AttendanceResponseDTO(
                a.getId(),
                a.getEmployee().getName(),
                a.getDate().toString(),
                a.getTimeIn() != null ? a.getTimeIn().toString() : null,
                a.getTimeOut() != null ? a.getTimeOut().toString() : null,
                a.getStatus()
        );
    }
}