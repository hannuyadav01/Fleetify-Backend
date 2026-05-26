package com.fleetify.entity;

import com.fleetify.enums.ServiceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// Complete maintenance and service history per vehicle.
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "garage_name")
    private String garageName;

    @Column(name = "odometer_at_service")
    private Integer odometerAtService;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Column(name = "next_service_km")
    private Integer nextServiceKm;

    @Column(name = "bill_url", columnDefinition = "TEXT")
    private String billUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    // ─── Getters & Setters ───────────────────────────────────────

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getGarageName() { return garageName; }
    public void setGarageName(String garageName) { this.garageName = garageName; }

    public Integer getOdometerAtService() { return odometerAtService; }
    public void setOdometerAtService(Integer odometerAtService) { this.odometerAtService = odometerAtService; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public LocalDate getNextServiceDate() { return nextServiceDate; }
    public void setNextServiceDate(LocalDate nextServiceDate) { this.nextServiceDate = nextServiceDate; }

    public Integer getNextServiceKm() { return nextServiceKm; }
    public void setNextServiceKm(Integer nextServiceKm) { this.nextServiceKm = nextServiceKm; }

    public String getBillUrl() { return billUrl; }
    public void setBillUrl(String billUrl) { this.billUrl = billUrl; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
