package com.staffguard.staffguard.schedule;

import com.staffguard.staffguard.user.User;
import com.staffguard.staffguard.user.UserRepository;
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

    public ScheduleResponse createSchedule(ScheduleRequest request) {
        User supervisor = userRepository.findById(request.getSupervisorId())
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        List<User> employees = userRepository.findAllById(request.getEmployeeIds());

        if (employees.size() < 3) {
            throw new RuntimeException("Minimum 3 employees required");
        }

        Schedule schedule = new Schedule();
        schedule.setSupervisor(supervisor);
        schedule.setEmployees(employees);
        schedule.setDate(LocalDate.parse(request.getDate()));
        schedule.setShiftStart(LocalTime.parse(request.getShiftStart()));
        schedule.setShiftEnd(LocalTime.parse(request.getShiftEnd()));

        return toResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAllByOrderByDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ScheduleResponse> getSchedulesByDate(String date) {
        return scheduleRepository.findByDate(LocalDate.parse(date))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<ScheduleResponse> getMyTodaySchedule(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return scheduleRepository
                .findByEmployeesContainingAndDate(employee, LocalDate.now())
                .map(this::toResponse);
    }

    public String deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found");
        }
        scheduleRepository.deleteById(id);
        return "Schedule deleted";
    }

    private ScheduleResponse toResponse(Schedule s) {
        List<String> employeeNames = s.getEmployees()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        return new ScheduleResponse(
                s.getId(),
                s.getSupervisor().getName(),
                employeeNames,
                s.getDate().toString(),
                s.getShiftStart().toString(),
                s.getShiftEnd().toString()
        );
    }
}