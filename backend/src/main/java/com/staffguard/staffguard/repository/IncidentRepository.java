package com.staffguard.staffguard.repository;

import com.staffguard.staffguard.model.Incident;
import com.staffguard.staffguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByEmployeeOrderByDateDesc(User employee);
    List<Incident> findAllByOrderByDateDesc();
}