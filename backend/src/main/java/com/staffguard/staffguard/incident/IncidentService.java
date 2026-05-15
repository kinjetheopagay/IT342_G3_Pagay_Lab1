package com.staffguard.staffguard.incident;

import com.staffguard.staffguard.user.User;
import com.staffguard.staffguard.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    public IncidentResponse submit(String email, IncidentRequest request) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Incident incident = new Incident();
        incident.setEmployee(employee);
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSupervisor(request.getSupervisor());
        incident.setDate(LocalDate.parse(request.getDate()));
        incident.setTime(request.getTime() != null ?
                LocalTime.parse(request.getTime()) : LocalTime.now());
        incident.setStatus("PENDING");
        incident.setImageUrl(request.getImageUrl());

        return toResponse(incidentRepository.save(incident));
    }

    public List<IncidentResponse> getMyIncidents(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return incidentRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAllByOrderByDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public IncidentResponse updateStatus(Long id, String status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        incident.setStatus(status.toUpperCase());
        return toResponse(incidentRepository.save(incident));
    }

    private IncidentResponse toResponse(Incident i) {
        return new IncidentResponse(
                i.getId(),
                i.getEmployee().getName(),
                i.getTitle(),
                i.getDescription(),
                i.getSupervisor(),
                i.getDate() != null ? i.getDate().toString() : null,
                i.getTime() != null ? i.getTime().toString() : null,
                i.getImageUrl(),
                i.getStatus()
        );
    }
}