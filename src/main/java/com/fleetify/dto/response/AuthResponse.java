package com.fleetify.dto.response;

import com.fleetify.enums.Role;

import java.util.UUID;

// Returned on successful login. Contains the JWT and essential user/company context
// so the frontend does not need a separate "me" endpoint immediately after login.
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private UUID userId;
    private String fullName;
    private String email;
    private Role role;
    private UUID companyId;   // null for SUPER_ADMIN

    public AuthResponse() {}

    public AuthResponse(String token, UUID userId, String fullName, String email, Role role, UUID companyId) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.companyId = companyId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
}
