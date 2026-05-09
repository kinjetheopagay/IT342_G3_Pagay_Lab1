package com.staffguard.staffguard.attendance;

import com.staffguard.staffguard.user.User;
import com.staffguard.staffguard.user.UserRepository;
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

    public AttendanceResponse timeIn(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
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

        return toResponse(attendanceRepository.save(attendance));
    }

    public AttendanceResponse timeOut(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Attendance attendance = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No time-in record found"));

        if (attendance.getTimeOut() != null) {
            throw new RuntimeException("Already timed out today");
        }

        attendance.setTimeOut(LocalTime.now());
        return toResponse(attendanceRepository.save(attendance));
    }

    public List<AttendanceResponse> getMyAttendance(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return attendanceRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAllByOrderByDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(
                a.getId(),
                a.getEmployee().getName(),
                a.getDate().toString(),
                a.getTimeIn() != null ? a.getTimeIn().toString() : null,
                a.getTimeOut() != null ? a.getTimeOut().toString() : null,
                a.getStatus()
        );
    }
}