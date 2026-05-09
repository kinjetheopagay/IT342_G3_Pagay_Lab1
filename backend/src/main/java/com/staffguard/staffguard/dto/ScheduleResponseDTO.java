package com.staffguard.staffguard.dto;

import java.util.List;

public class ScheduleResponseDTO {
    private Long id;
    private String supervisorName;
    private List<String> employeeNames;
    private String date;
    private String shiftStart;
    private String shiftEnd;

    public ScheduleResponseDTO(Long id, String supervisorName,
                                List<String> employeeNames, String date,
                                String shiftStart, String shiftEnd) {
        this.id = id;
        this.supervisorName = supervisorName;
        this.employeeNames = employeeNames;
        this.date = date;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
    }

    public Long getId() { return id; }
    public String getSupervisorName() { return supervisorName; }
    public List<String> getEmployeeNames() { return employeeNames; }
    public String getDate() { return date; }
    public String getShiftStart() { return shiftStart; }
    public String getShiftEnd() { return shiftEnd; }
}