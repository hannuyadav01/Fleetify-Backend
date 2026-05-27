package com.fleetify.dto.request;

import com.fleetify.enums.PaymentStatus;
import com.fleetify.enums.TripStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TripRequest {

    private UUID companyId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotNull(message = "Driver ID is required")
    private UUID driverId;

    private UUID customerId;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    private String routeDetails;

    @DecimalMin(value = "0.0", message = "Distance must be positive")
    private BigDecimal distanceKm;

    private LocalDateTime scheduledStart;
    private LocalDateTime actualStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualEnd;

    private TripStatus status = TripStatus.SCHEDULED;

    @DecimalMin(value = "0.0", message = "Freight amount must be positive")
    private BigDecimal freightAmount;

    @DecimalMin(value = "0.0", message = "Advance paid must be positive")
    private BigDecimal advancePaid;

    private BigDecimal balanceDue;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String goodsType;

    @DecimalMin(value = "0.0", message = "Weight must be positive")
    private BigDecimal weightTons;

    private String trackingCode;
    private String notes;

    // Getters and Setters
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getRouteDetails() { return routeDetails; }
    public void setRouteDetails(String routeDetails) { this.routeDetails = routeDetails; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
    public LocalDateTime getActualStart() { return actualStart; }
    public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }
    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    public LocalDateTime getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }
    public BigDecimal getAdvancePaid() { return advancePaid; }
    public void setAdvancePaid(BigDecimal advancePaid) { this.advancePaid = advancePaid; }
    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getGoodsType() { return goodsType; }
    public void setGoodsType(String goodsType) { this.goodsType = goodsType; }
    public BigDecimal getWeightTons() { return weightTons; }
    public void setWeightTons(BigDecimal weightTons) { this.weightTons = weightTons; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
