package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.IncidentRequestDTO;
import com.staffguard.staffguard.dto.IncidentResponseDTO;
import com.staffguard.staffguard.model.Incident;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.IncidentRepository;
import com.staffguard.staffguard.repository.UserRepository;
import com.staffguard.staffguard.exception.UserNotFoundException;
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

    public IncidentResponseDTO submitIncident(String email, IncidentRequestDTO dto) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Incident incident = new Incident();
        incident.setEmployee(employee);
        incident.setTitle(dto.getTitle());
        incident.setSupervisor(dto.getSupervisor());
        incident.setDate(LocalDate.parse(dto.getDate()));
        incident.setTime(dto.getTime() != null ?
                LocalTime.parse(dto.getTime()) : LocalTime.now());
        incident.setStatus("PENDING");
        incident.setImageUrl(dto.getImageUrl());

        Incident saved = incidentRepository.save(incident);
        return toDTO(saved);
    }

    public List<IncidentResponseDTO> getMyIncidents(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return incidentRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<IncidentResponseDTO> getAllIncidents() {
        return incidentRepository.findAllByOrderByDateDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public IncidentResponseDTO updateStatus(Long id, String status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        incident.setStatus(status.toUpperCase());
        return toDTO(incidentRepository.save(incident));
    }

    private IncidentResponseDTO toDTO(Incident incident) {
        return new IncidentResponseDTO(
                incident.getId(),
                incident.getEmployee().getName(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSupervisor(),
                incident.getDate() != null ? incident.getDate().toString() : null,
                incident.getTime() != null ? incident.getTime().toString() : null,
                incident.getImageUrl(),
                incident.getStatus()
        );
    }
}