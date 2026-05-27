package com.fleetify.entity;

import com.fleetify.enums.DriverStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// Driver profile. Every driver also has a linked User account for app login.
// Created atomically with their User row in a single @Transactional call.
@Entity
@Table(name = "drivers")
public class Driver extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Every driver has exactly one User account (for mobile app login)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    // Fixed monthly salary in INR
    @Column(name = "monthly_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlySalary;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
private String licenseNumber;

@Column(name = "license_expiry", nullable = false)
private LocalDate licenseExpiry;

@Column(name = "license_type", length = 20)
private String licenseType; // HMV, LMV, HGV

@Column(name = "aadhaar_number", length = 20)
private String aadhaarNumber; // store encrypted

@Column(name = "pan_number", length = 15)
private String panNumber; // store encrypted

    // AVAILABLE, ON_TRIP, ON_LEAVE, INACTIVE
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DriverStatus status;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    // Soft delete
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // ─── Getters & Setters ───────────────────────────────────────

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public BigDecimal getMonthlySalary() { return monthlySalary; }
    public void setMonthlySalary(BigDecimal monthlySalary) { this.monthlySalary = monthlySalary; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getLicenseNumber() { return licenseNumber; }
public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

public LocalDate getLicenseExpiry() { return licenseExpiry; }
public void setLicenseExpiry(LocalDate licenseExpiry) { this.licenseExpiry = licenseExpiry; }

public String getLicenseType() { return licenseType; }
public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

public String getAadhaarNumber() { return aadhaarNumber; }
public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

public String getPanNumber() { return panNumber; }
public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
}
