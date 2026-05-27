package com.fleetify.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FuelLogRequest {

    private UUID companyId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    private UUID tripId;

    @NotNull(message = "Quantity in litres is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantityLitres;

    @NotNull(message = "Cost per litre is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost per litre must be greater than zero")
    private BigDecimal costPerLitre;

    private BigDecimal totalCost;

    private String fuelStation;

    @NotNull(message = "Odometer reading at fill is required")
    @Min(value = 0, message = "Odometer reading must be positive")
    private Integer odometerAtFill;

    private String receiptUrl;

    @NotNull(message = "Fueling timestamp (filledAt) is required")
    private LocalDateTime filledAt;

    // Getters and Setters
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
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
    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public LocalDateTime getFilledAt() { return filledAt; }
    public void setFilledAt(LocalDateTime filledAt) { this.filledAt = filledAt; }
}
