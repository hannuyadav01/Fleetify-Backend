package com.fleetify.entity;

import com.fleetify.enums.AssignmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Audit trail that records which driver was assigned to which vehicle during a trip/time window.
// endDate = null means the assignment is currently active.
@Entity
@Table(name = "driver_vehicle_assignments")
public class DriverVehicleAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Nullable — can assign before a trip is formally created
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = true)
    private Trip trip;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // Null = assignment is still active
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "start_odometer")
    private Integer startOdometer;

    @Column(name = "end_odometer")
    private Integer endOdometer;

    // ACTIVE, COMPLETED, CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ─── Getters & Setters ───────────────────────────────────────

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getStartOdometer() { return startOdometer; }
    public void setStartOdometer(Integer startOdometer) { this.startOdometer = startOdometer; }

    public Integer getEndOdometer() { return endOdometer; }
    public void setEndOdometer(Integer endOdometer) { this.endOdometer = endOdometer; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
