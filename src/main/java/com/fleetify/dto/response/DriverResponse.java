package com.fleetify.dto.response;

import com.fleetify.entity.Driver;
import com.fleetify.enums.DriverStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DriverResponse {

    private UUID id;
    private UUID companyId;
    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private String emergencyContact;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private BigDecimal monthlySalary;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private String licenseType;
    private DriverStatus status;
    private String profilePhotoUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method
    public static DriverResponse from(Driver driver) {
        DriverResponse res = new DriverResponse();
        res.id = driver.getId();
        res.companyId = driver.getCompany() != null ? driver.getCompany().getId() : null;
        res.userId = driver.getUser() != null ? driver.getUser().getId() : null;
        res.fullName = driver.getFullName();
        res.email = driver.getUser() != null ? driver.getUser().getEmail() : null;
        res.phone = driver.getPhone();
        res.emergencyContact = driver.getEmergencyContact();
        res.address = driver.getAddress();
        res.dateOfBirth = driver.getDateOfBirth();
        res.joiningDate = driver.getJoiningDate();
        res.monthlySalary = driver.getMonthlySalary();
        res.licenseNumber = driver.getLicenseNumber();
        res.licenseExpiry = driver.getLicenseExpiry();
        res.licenseType = driver.getLicenseType();
        res.status = driver.getStatus();
        res.profilePhotoUrl = driver.getProfilePhotoUrl();
        res.isActive = driver.isActive();
        res.createdAt = driver.getCreatedAt();
        res.updatedAt = driver.getUpdatedAt();
        return res;
    }

    // ─── Getters ───────────────────────────────────────────────────

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getAddress() { return address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public BigDecimal getMonthlySalary() { return monthlySalary; }
    public String getLicenseNumber() { return licenseNumber; }
    public LocalDate getLicenseExpiry() { return licenseExpiry; }
    public String getLicenseType() { return licenseType; }
    public DriverStatus getStatus() { return status; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
