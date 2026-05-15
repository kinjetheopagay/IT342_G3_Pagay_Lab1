package com.staffguard.staffguard.incident;

import com.staffguard.staffguard.shared.util.JwtUtil;
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

    @PostMapping
    public IncidentResponse submit(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody IncidentRequest request) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return incidentService.submit(email, request);
    }

    @GetMapping("/my")
    public List<IncidentResponse> getMyIncidents(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return incidentService.getMyIncidents(email);
    }

    @GetMapping("/all")
    public List<IncidentResponse> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @PutMapping("/{id}/status")
    public IncidentResponse updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return incidentService.updateStatus(id, status);
    }
}