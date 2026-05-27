package com.fleetify.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Specific log for vehicle fueling, distinct from general expenses.
@Entity
@Table(name = "fuel_logs")
public class FuelLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = true)
    private Trip trip;

    @Column(name = "quantity_litres", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityLitres;

    @Column(name = "cost_per_litre", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerLitre;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "fuel_station")
    private String fuelStation;

    @Column(name = "odometer_at_fill", nullable = false)
    private Integer odometerAtFill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_by_id")
    private User filledBy;

    @Column(name = "receipt_url", columnDefinition = "TEXT")
    private String receiptUrl;

    @Column(name = "filled_at", nullable = false)
    private LocalDateTime filledAt;

    // ─── Getters & Setters ───────────────────────────────────────

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

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

    public User getFilledBy() { return filledBy; }
    public void setFilledBy(User filledBy) { this.filledBy = filledBy; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }

    public LocalDateTime getFilledAt() { return filledAt; }
    public void setFilledAt(LocalDateTime filledAt) { this.filledAt = filledAt; }
}
