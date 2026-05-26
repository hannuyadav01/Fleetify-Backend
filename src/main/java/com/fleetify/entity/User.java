package com.fleetify.entity;

import com.fleetify.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// System user — every person who logs into Fleetify has a row here.
// Roles: SUPER_ADMIN (no company), ADMIN, FLEET_MANAGER, ACCOUNTANT, DRIVER, CUSTOMER.
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    // Null only for SUPER_ADMIN accounts — all other roles belong to a company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Used for login — must be unique across the entire platform
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    // BCrypt hash — NEVER returned in API responses
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // ADMIN, FLEET_MANAGER, DRIVER, ACCOUNTANT, CUSTOMER, SUPER_ADMIN
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Firebase Cloud Messaging token for push notifications on mobile app
    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    // Soft delete — deactivated users cannot log in
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ─── Getters & Setters ───────────────────────────────────────

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
