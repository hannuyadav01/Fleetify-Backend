package com.fleetify.entity;

import com.fleetify.enums.FuelType;
import com.fleetify.enums.VehicleStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// Core asset entity. Each row is one physical vehicle (truck, tempo, bus) owned by a company.
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Vehicle registration number — unique across the platform (e.g. "HR55AB1234")
    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    // Brand name (Tata, Ashok Leyland, Mahindra, etc.)
    @Column(name = "make", nullable = false, length = 100)
    private String make;

    // Model (407, 1613, Blazo 35, etc.)
    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "year")
    private Integer year;

    // Truck, Tempo, Bus, Tanker, etc.
    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "load_capacity_tons", precision = 10, scale = 2)
    private BigDecimal loadCapacityTons;

    // Updated after every trip completion
    @Column(name = "current_odometer_km")
    private Integer currentOdometerKm;

    // ACTIVE, ON_TRIP, MAINTENANCE, INACTIVE
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    // Soft delete
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // ─── Getters & Setters ───────────────────────────────────────

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

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

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
