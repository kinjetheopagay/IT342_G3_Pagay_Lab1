package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.IncidentRequestDTO;
import com.staffguard.staffguard.dto.IncidentResponseDTO;
import com.staffguard.staffguard.service.IncidentService;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin
public class IncidentController {

    private final IncidentService incidentService;
    private final JwtUtil jwtUtil;

    public IncidentController(IncidentService incidentService, JwtUtil jwtUtil) {
        this.incidentService = incidentService;
        this.jwtUtil = jwtUtil;
    }

    // Employee submits incident
    @PostMapping
    public IncidentResponseDTO submitIncident(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody IncidentRequestDTO dto) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return incidentService.submitIncident(email, dto);
    }

    // Employee views their incidents
    @GetMapping("/my")
    public List<IncidentResponseDTO> getMyIncidents(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return incidentService.getMyIncidents(email);
    }

    // Admin views all incidents
    @GetMapping("/all")
    public List<IncidentResponseDTO> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // Admin approves or rejects
    @PutMapping("/{id}/status")
    public IncidentResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return incidentService.updateStatus(id, status);
    }
}