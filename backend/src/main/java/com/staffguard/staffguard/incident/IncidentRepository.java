package com.staffguard.staffguard.incident;

import com.staffguard.staffguard.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByEmployeeOrderByDateDesc(User employee);
    List<Incident> findAllByOrderByDateDesc();
}