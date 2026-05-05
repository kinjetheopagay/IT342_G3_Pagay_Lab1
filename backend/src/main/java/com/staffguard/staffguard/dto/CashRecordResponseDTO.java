package com.staffguard.staffguard.dto;

public class CashRecordResponseDTO {
    private Long id;
    private String employeeName;
    private String supervisorName;
    private String date;
    private String pos;
    private Double totalSales;
    private Double amount;
    private String status;

    public CashRecordResponseDTO(Long id, String employeeName, String supervisorName,
                                  String date, String pos, Double totalSales,
                                  Double amount, String status) {
        this.id = id;
        this.employeeName = employeeName;
        this.supervisorName = supervisorName;
        this.date = date;
        this.pos = pos;
        this.totalSales = totalSales;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getEmployeeName() { return employeeName; }
    public String getSupervisorName() { return supervisorName; }
    public String getDate() { return date; }
    public String getPos() { return pos; }
    public Double getTotalSales() { return totalSales; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }
}