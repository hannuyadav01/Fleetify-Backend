package com.fleetify.entity;

import com.fleetify.enums.PaymentStatus;
import com.fleetify.enums.TripStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// A trip is a single point-to-point freight movement: source → destination.
// This is the financial core of Fleetify — freight amount, advance, balance, and payment status all live here.
@Entity
@Table(name = "trips")
public class Trip extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // Link back to the driver-vehicle pairing for this trip
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private DriverVehicleAssignment assignment;

    // Walk-in customer (optional — may not be a registered user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private User customer;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "destination", nullable = false)
    private String destination;

    // Route stops, toll details, instructions etc.
    @Column(name = "route_details", columnDefinition = "TEXT")
    private String routeDetails;

    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus status;

    // Total freight agreed with customer in INR
    @Column(name = "freight_amount", precision = 12, scale = 2)
    private BigDecimal freightAmount;

    // Advance collected at trip start
    @Column(name = "advance_paid", precision = 12, scale = 2)
    private BigDecimal advancePaid;

    // freightAmount - advancePaid - subsequent payments
    @Column(name = "balance_due", precision = 12, scale = 2)
    private BigDecimal balanceDue;

    // PENDING, PARTIAL, PAID
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    // What goods are being transported (coal, steel, FMCG, etc.)
    @Column(name = "goods_type")
    private String goodsType;

    @Column(name = "weight_tons", precision = 10, scale = 2)
    private BigDecimal weightTons;

    // Short alphanumeric code shared with customer for live tracking
    @Column(name = "tracking_code", unique = true, length = 20)
    private String trackingCode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // ─── Getters & Setters ───────────────────────────────────────

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public DriverVehicleAssignment getAssignment() { return assignment; }
    public void setAssignment(DriverVehicleAssignment assignment) { this.assignment = assignment; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

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

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
