package com.staffguard.staffguard.dto;

import java.util.List;

public class ScheduleRequestDTO {
    private Long supervisorId;
    private List<Long> employeeIds;
    private String date;
    private String shiftStart;
    private String shiftEnd;

    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public List<Long> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getShiftStart() { return shiftStart; }
    public void setShiftStart(String shiftStart) { this.shiftStart = shiftStart; }

    public String getShiftEnd() { return shiftEnd; }
    public void setShiftEnd(String shiftEnd) { this.shiftEnd = shiftEnd; }
}