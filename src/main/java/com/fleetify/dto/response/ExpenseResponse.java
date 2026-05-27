package com.fleetify.dto.response;

import com.fleetify.entity.Expense;
import com.fleetify.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExpenseResponse {

    private UUID id;
    private UUID companyId;
    private UUID tripId;
    private UUID vehicleId;
    private String vehicleNumber;
    private ExpenseType expenseType;
    private BigDecimal amount;
    private String description;
    private String receiptUrl;
    private LocalDate expenseDate;
    private UUID createdByUserId;
    private String createdByUserName;
    private boolean isVerified;
    private UUID verifiedByUserId;
    private String verifiedByUserName;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExpenseResponse from(Expense expense) {
        ExpenseResponse res = new ExpenseResponse();
        res.id = expense.getId();
        res.companyId = expense.getCompany() != null ? expense.getCompany().getId() : null;
        res.tripId = expense.getTrip() != null ? expense.getTrip().getId() : null;
        if (expense.getVehicle() != null) {
            res.vehicleId = expense.getVehicle().getId();
            res.vehicleNumber = expense.getVehicle().getVehicleNumber();
        }
        res.expenseType = expense.getExpenseType();
        res.amount = expense.getAmount();
        res.description = expense.getDescription();
        res.receiptUrl = expense.getReceiptUrl();
        res.expenseDate = expense.getExpenseDate();
        if (expense.getCreatedBy() != null) {
            res.createdByUserId = expense.getCreatedBy().getId();
            res.createdByUserName = expense.getCreatedBy().getFullName();
        }
        res.isVerified = expense.isVerified();
        if (expense.getVerifiedBy() != null) {
            res.verifiedByUserId = expense.getVerifiedBy().getId();
            res.verifiedByUserName = expense.getVerifiedBy().getFullName();
        }
        res.verifiedAt = expense.getVerifiedAt();
        res.createdAt = expense.getCreatedAt();
        res.updatedAt = expense.getUpdatedAt();
        return res;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getTripId() { return tripId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public ExpenseType getExpenseType() { return expenseType; }
    public void setExpenseType(ExpenseType expenseType) { this.expenseType = expenseType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }
    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public UUID getVerifiedByUserId() { return verifiedByUserId; }
    public void setVerifiedByUserId(UUID verifiedByUserId) { this.verifiedByUserId = verifiedByUserId; }
    public String getVerifiedByUserName() { return verifiedByUserName; }
    public void setVerifiedByUserName(String verifiedByUserName) { this.verifiedByUserName = verifiedByUserName; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
