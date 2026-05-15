package com.staffguard.staffguard.schedule;

import com.staffguard.staffguard.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByOrderByDateDesc();
    List<Schedule> findByDate(LocalDate date);
    Optional<Schedule> findByEmployeesContainingAndDate(User employee, LocalDate date);
}