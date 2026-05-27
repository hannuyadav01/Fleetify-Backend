package com.fleetify.dto.response;

import com.fleetify.entity.Vehicle;
import com.fleetify.enums.FuelType;
import com.fleetify.enums.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleResponse {

    private UUID id;
    private UUID companyId;
    private String vehicleNumber;
    private String make;
    private String model;
    private Integer year;
    private String vehicleType;
    private FuelType fuelType;
    private BigDecimal loadCapacityTons;
    private Integer currentOdometerKm;
    private VehicleStatus status;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VehicleResponse from(Vehicle v) {
        if (v == null) return null;
        VehicleResponse r = new VehicleResponse();
        r.id = v.getId();
        r.companyId = v.getCompany() != null ? v.getCompany().getId() : null;
        r.vehicleNumber = v.getVehicleNumber();
        r.make = v.getMake();
        r.model = v.getModel();
        r.year = v.getYear();
        r.vehicleType = v.getVehicleType();
        r.fuelType = v.getFuelType();
        r.loadCapacityTons = v.getLoadCapacityTons();
        r.currentOdometerKm = v.getCurrentOdometerKm();
        r.status = v.getStatus();
        r.purchaseDate = v.getPurchaseDate();
        r.purchasePrice = v.getPurchasePrice();
        r.isActive = v.isActive();
        r.createdAt = v.getCreatedAt();
        r.updatedAt = v.getUpdatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getVehicleType() { return vehicleType; }
    public FuelType getFuelType() { return fuelType; }
    public BigDecimal getLoadCapacityTons() { return loadCapacityTons; }
    public Integer getCurrentOdometerKm() { return currentOdometerKm; }
    public VehicleStatus getStatus() { return status; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
