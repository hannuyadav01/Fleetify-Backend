package com.fleetify.dto.response;

import com.fleetify.entity.Trip;
import com.fleetify.enums.PaymentStatus;
import com.fleetify.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TripResponse {

    private UUID id;
    private UUID companyId;
    private UUID vehicleId;
    private String vehicleNumber;
    private UUID driverId;
    private String driverName;
    private UUID assignmentId;
    private UUID customerId;
    private String customerName;
    private String source;
    private String destination;
    private String routeDetails;
    private BigDecimal distanceKm;
    private LocalDateTime scheduledStart;
    private LocalDateTime actualStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualEnd;
    private TripStatus status;
    private BigDecimal freightAmount;
    private BigDecimal advancePaid;
    private BigDecimal balanceDue;
    private PaymentStatus paymentStatus;
    private String goodsType;
    private BigDecimal weightTons;
    private String trackingCode;
    private String notes;
    private UUID createdByUserId;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TripResponse from(Trip trip) {
        TripResponse res = new TripResponse();
        res.id = trip.getId();
        res.companyId = trip.getCompany() != null ? trip.getCompany().getId() : null;
        if (trip.getVehicle() != null) {
            res.vehicleId = trip.getVehicle().getId();
            res.vehicleNumber = trip.getVehicle().getVehicleNumber();
        }
        if (trip.getDriver() != null) {
            res.driverId = trip.getDriver().getId();
            res.driverName = trip.getDriver().getFullName();
        }
        if (trip.getAssignment() != null) {
            res.assignmentId = trip.getAssignment().getId();
        }
        if (trip.getCustomer() != null) {
            res.customerId = trip.getCustomer().getId();
            res.customerName = trip.getCustomer().getFullName();
        }
        res.source = trip.getSource();
        res.destination = trip.getDestination();
        res.routeDetails = trip.getRouteDetails();
        res.distanceKm = trip.getDistanceKm();
        res.scheduledStart = trip.getScheduledStart();
        res.actualStart = trip.getActualStart();
        res.scheduledEnd = trip.getScheduledEnd();
        res.actualEnd = trip.getActualEnd();
        res.status = trip.getStatus();
        res.freightAmount = trip.getFreightAmount();
        res.advancePaid = trip.getAdvancePaid();
        res.balanceDue = trip.getBalanceDue();
        res.paymentStatus = trip.getPaymentStatus();
        res.goodsType = trip.getGoodsType();
        res.weightTons = trip.getWeightTons();
        res.trackingCode = trip.getTrackingCode();
        res.notes = trip.getNotes();
        if (trip.getCreatedBy() != null) {
            res.createdByUserId = trip.getCreatedBy().getId();
            res.createdByUserName = trip.getCreatedBy().getFullName();
        }
        res.createdAt = trip.getCreatedAt();
        res.updatedAt = trip.getUpdatedAt();
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
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
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
    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }
    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
