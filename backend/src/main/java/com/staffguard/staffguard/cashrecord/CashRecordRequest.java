package com.staffguard.staffguard.cashrecord;

public class CashRecordRequest {
    private String date;
    private String pos;
    private Double totalSales;
    private Double amount;
    private String status;
    private Long supervisorId;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPos() { return pos; }
    public void setPos(String pos) { this.pos = pos; }
    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
}