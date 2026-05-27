package com.fleetify.dto.response;

import com.fleetify.entity.SalaryRecord;
import com.fleetify.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SalaryRecordResponse {

    private UUID id;
    private UUID driverId;
    private String driverName;
    private UUID companyId;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private UUID paidByUserId;
    private String paidByUserName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SalaryRecordResponse from(SalaryRecord record) {
        SalaryRecordResponse res = new SalaryRecordResponse();
        res.id = record.getId();
        if (record.getDriver() != null) {
            res.driverId = record.getDriver().getId();
            res.driverName = record.getDriver().getFullName();
        }
        res.companyId = record.getCompany() != null ? record.getCompany().getId() : null;
        res.month = record.getMonth();
        res.year = record.getYear();
        res.baseSalary = record.getBaseSalary();
        res.allowances = record.getAllowances();
        res.deductions = record.getDeductions();
        res.netSalary = record.getNetSalary();
        res.paymentStatus = record.getPaymentStatus();
        res.paymentDate = record.getPaymentDate();
        if (record.getPaidBy() != null) {
            res.paidByUserId = record.getPaidBy().getId();
            res.paidByUserName = record.getPaidBy().getFullName();
        }
        res.notes = record.getNotes();
        res.createdAt = record.getCreatedAt();
        res.updatedAt = record.getUpdatedAt();
        return res;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public UUID getPaidByUserId() { return paidByUserId; }
    public void setPaidByUserId(UUID paidByUserId) { this.paidByUserId = paidByUserId; }
    public String getPaidByUserName() { return paidByUserName; }
    public void setPaidByUserName(String paidByUserName) { this.paidByUserName = paidByUserName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
