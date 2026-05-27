package com.fleetify.entity;

import com.fleetify.enums.CompanyStatus;
import jakarta.persistence.*;

// Root multi-tenancy entity. Every other tenant-specific entity has a company_id FK pointing here.
// One company = one fleet business (e.g. "Sharma Transport Pvt Ltd").
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    // Legal/display name of the fleet business
    @Column(name = "name", nullable = false)
    private String name;

    // GST registration number — used on invoices
    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    // PAN number for the company
    @Column(name = "pan_number", length = 15)
    private String panNumber;

    // Full registered address
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email")
    private String email;

    // S3 or Cloudflare R2 URL for company logo
    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    // ACTIVE, SUSPENDED, TRIAL, INACTIVE
    @Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false)
private CompanyStatus status = CompanyStatus.TRIAL;

    // Soft delete flag — deactivated companies are excluded from all queries
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // ─── Getters & Setters ───────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public CompanyStatus getStatus() { return status; }
    public void setStatus(CompanyStatus status) { this.status = status; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
