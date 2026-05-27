package com.fleetify.dto.request;

import com.fleetify.enums.FuelType;
import com.fleetify.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class VehicleRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    private Integer year;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    private FuelType fuelType;

    private BigDecimal loadCapacityTons;

    private Integer currentOdometerKm;

    @NotNull(message = "Status is required")
    private VehicleStatus status;

    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;

    // Getters and Setters
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public BigDecimal getLoadCapacityTons() { return loadCapacityTons; }
    public void setLoadCapacityTons(BigDecimal loadCapacityTons) { this.loadCapacityTons = loadCapacityTons; }

    public Integer getCurrentOdometerKm() { return currentOdometerKm; }
    public void setCurrentOdometerKm(Integer currentOdometerKm) { this.currentOdometerKm = currentOdometerKm; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
}
