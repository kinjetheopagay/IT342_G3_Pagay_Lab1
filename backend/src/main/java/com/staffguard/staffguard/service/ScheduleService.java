package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.ScheduleRequestDTO;
import com.staffguard.staffguard.dto.ScheduleResponseDTO;
import com.staffguard.staffguard.model.Schedule;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.ScheduleRepository;
import com.staffguard.staffguard.repository.UserRepository;
import com.staffguard.staffguard.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    // Admin creates a schedule
    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO dto) {
        User supervisor = userRepository.findById(dto.getSupervisorId())
                .orElseThrow(() -> new UserNotFoundException("Supervisor not found"));

        List<User> employees = userRepository.findAllById(dto.getEmployeeIds());

        if (employees.size() < 3) {
            throw new RuntimeException("Minimum 3 employees required per shift");
        }

        Schedule schedule = new Schedule();
        schedule.setSupervisor(supervisor);
        schedule.setEmployees(employees);
        schedule.setDate(LocalDate.parse(dto.getDate()));
        schedule.setShiftStart(LocalTime.parse(dto.getShiftStart()));
        schedule.setShiftEnd(LocalTime.parse(dto.getShiftEnd()));

        return toDTO(scheduleRepository.save(schedule));
    }

    // Get all schedules
    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleRepository.findAllByOrderByDateDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Get schedules by date
    public List<ScheduleResponseDTO> getSchedulesByDate(String date) {
        return scheduleRepository.findByDate(LocalDate.parse(date))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Get today's schedule for an employee
    public Optional<ScheduleResponseDTO> getMyTodaySchedule(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return scheduleRepository
                .findByEmployeesContainingAndDate(employee, LocalDate.now())
                .map(this::toDTO);
    }

    // Delete a schedule
    public String deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found");
        }
        scheduleRepository.deleteById(id);
        return "Schedule deleted";
    }

    private ScheduleResponseDTO toDTO(Schedule s) {
        List<String> employeeNames = s.getEmployees()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        return new ScheduleResponseDTO(
                s.getId(),
                s.getSupervisor().getName(),
                employeeNames,
                s.getDate().toString(),
                s.getShiftStart().toString(),
                s.getShiftEnd().toString()
        );
    }
}