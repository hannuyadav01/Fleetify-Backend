package com.fleetify.dto.response;

import com.fleetify.entity.Company;
import com.fleetify.enums.CompanyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanyResponse {

    private UUID id;
    private String name;
    private String gstNumber;
    private String panNumber;
    private String address;
    private String phone;
    private String email;
    private String logoUrl;
    private CompanyStatus status;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CompanyResponse from(Company c) {
        CompanyResponse r = new CompanyResponse();
        r.id = c.getId();
        r.name = c.getName();
        r.gstNumber = c.getGstNumber();
        r.panNumber = c.getPanNumber();
        r.address = c.getAddress();
        r.phone = c.getPhone();
        r.email = c.getEmail();
        r.logoUrl = c.getLogoUrl();
        r.status = c.getStatus();
        r.isActive = c.isActive();
        r.createdAt = c.getCreatedAt();
        r.updatedAt = c.getUpdatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getGstNumber() { return gstNumber; }
    public String getPanNumber() { return panNumber; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getLogoUrl() { return logoUrl; }
    public CompanyStatus getStatus() { return status; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
