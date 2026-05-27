package com.fleetify.dto.response;

import com.fleetify.entity.Alert;
import com.fleetify.enums.AlertSeverity;
import com.fleetify.enums.AlertType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AlertResponse {

    private UUID id;
    private UUID companyId;
    private UUID vehicleId;
    private String vehicleNumber;
    private UUID documentId;
    private UUID driverId;
    private String driverName;
    private AlertType alertType;
    private AlertSeverity severity;
    private String message;
    private LocalDate dueDate;
    private boolean isResolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AlertResponse from(Alert alert) {
        AlertResponse res = new AlertResponse();
        res.id = alert.getId();
        res.companyId = alert.getCompany() != null ? alert.getCompany().getId() : null;
        if (alert.getVehicle() != null) {
            res.vehicleId = alert.getVehicle().getId();
            res.vehicleNumber = alert.getVehicle().getVehicleNumber();
        }
        if (alert.getDocument() != null) {
            res.documentId = alert.getDocument().getId();
        }
        if (alert.getDriver() != null) {
            res.driverId = alert.getDriver().getId();
            res.driverName = alert.getDriver().getFullName();
        }
        res.alertType = alert.getAlertType();
        res.severity = alert.getSeverity();
        res.message = alert.getMessage();
        res.dueDate = alert.getDueDate();
        res.isResolved = alert.isResolved();
        res.resolvedAt = alert.getResolvedAt();
        res.createdAt = alert.getCreatedAt();
        res.updatedAt = alert.getUpdatedAt();
        return res;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
