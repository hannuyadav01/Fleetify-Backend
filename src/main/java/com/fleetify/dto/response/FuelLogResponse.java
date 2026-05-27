package com.fleetify.dto.response;

import com.fleetify.entity.FuelLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FuelLogResponse {

    private UUID id;
    private UUID vehicleId;
    private String vehicleNumber;
    private UUID companyId;
    private UUID tripId;
    private BigDecimal quantityLitres;
    private BigDecimal costPerLitre;
    private BigDecimal totalCost;
    private String fuelStation;
    private Integer odometerAtFill;
    private UUID filledByUserId;
    private String filledByUserName;
    private String receiptUrl;
    private LocalDateTime filledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FuelLogResponse from(FuelLog log) {
        FuelLogResponse res = new FuelLogResponse();
        res.id = log.getId();
        if (log.getVehicle() != null) {
            res.vehicleId = log.getVehicle().getId();
            res.vehicleNumber = log.getVehicle().getVehicleNumber();
        }
        res.companyId = log.getCompany() != null ? log.getCompany().getId() : null;
        res.tripId = log.getTrip() != null ? log.getTrip().getId() : null;
        res.quantityLitres = log.getQuantityLitres();
        res.costPerLitre = log.getCostPerLitre();
        res.totalCost = log.getTotalCost();
        res.fuelStation = log.getFuelStation();
        res.odometerAtFill = log.getOdometerAtFill();
        if (log.getFilledBy() != null) {
            res.filledByUserId = log.getFilledBy().getId();
            res.filledByUserName = log.getFilledBy().getFullName();
        }
        res.receiptUrl = log.getReceiptUrl();
        res.filledAt = log.getFilledAt();
        res.createdAt = log.getCreatedAt();
        res.updatedAt = log.getUpdatedAt();
        return res;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getTripId() { return tripId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }
    public BigDecimal getQuantityLitres() { return quantityLitres; }
    public void setQuantityLitres(BigDecimal quantityLitres) { this.quantityLitres = quantityLitres; }
    public BigDecimal getCostPerLitre() { return costPerLitre; }
    public void setCostPerLitre(BigDecimal costPerLitre) { this.costPerLitre = costPerLitre; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getFuelStation() { return fuelStation; }
    public void setFuelStation(String fuelStation) { this.fuelStation = fuelStation; }
    public Integer getOdometerAtFill() { return odometerAtFill; }
    public void setOdometerAtFill(Integer odometerAtFill) { this.odometerAtFill = odometerAtFill; }
    public UUID getFilledByUserId() { return filledByUserId; }
    public void setFilledByUserId(UUID filledByUserId) { this.filledByUserId = filledByUserId; }
    public String getFilledByUserName() { return filledByUserName; }
    public void setFilledByUserName(String filledByUserName) { this.filledByUserName = filledByUserName; }
    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public LocalDateTime getFilledAt() { return filledAt; }
    public void setFilledAt(LocalDateTime filledAt) { this.filledAt = filledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
