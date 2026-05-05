package com.staffguard.staffguard.dto;

public class AttendanceResponseDTO {
    private Long id;
    private String employeeName;
    private String date;
    private String timeIn;
    private String timeOut;
    private String status;

    public AttendanceResponseDTO(Long id, String employeeName, String date,
                                  String timeIn, String timeOut, String status) {
        this.id = id;
        this.employeeName = employeeName;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public String getTimeIn() { return timeIn; }
    public String getTimeOut() { return timeOut; }
    public String getStatus() { return status; }
}