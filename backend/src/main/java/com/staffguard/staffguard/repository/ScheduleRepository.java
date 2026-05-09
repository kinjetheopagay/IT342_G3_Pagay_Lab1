package com.staffguard.staffguard.repository;

import com.staffguard.staffguard.model.Schedule;
import com.staffguard.staffguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByOrderByDateDesc();
    List<Schedule> findByDate(LocalDate date);
    Optional<Schedule> findByEmployeesContainingAndDate(User employee, LocalDate date);
}