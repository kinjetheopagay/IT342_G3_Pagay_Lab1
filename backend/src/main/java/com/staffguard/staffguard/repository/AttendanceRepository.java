package com.staffguard.staffguard.repository;

import com.staffguard.staffguard.model.Attendance;
import com.staffguard.staffguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeOrderByDateDesc(User employee);
    List<Attendance> findAllByOrderByDateDesc();
    Optional<Attendance> findByEmployeeAndDate(User employee, LocalDate date);
}