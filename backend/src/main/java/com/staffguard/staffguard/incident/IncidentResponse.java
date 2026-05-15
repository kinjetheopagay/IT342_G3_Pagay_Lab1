package com.staffguard.staffguard.incident;

public class IncidentResponse {
    private Long id;
    private String employeeName;
    private String title;
    private String description;
    private String supervisor;
    private String date;
    private String time;
    private String imageUrl;
    private String status;

    public IncidentResponse(Long id, String employeeName, String title,
                             String description, String supervisor,
                             String date, String time,
                             String imageUrl, String status) {
        this.id = id;
        this.employeeName = employeeName;
        this.title = title;
        this.description = description;
        this.supervisor = supervisor;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getEmployeeName() { return employeeName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSupervisor() { return supervisor; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
}