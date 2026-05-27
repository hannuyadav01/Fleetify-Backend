package com.fleetify.controller;

import com.fleetify.dto.request.CompanyRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.CompanyResponse;
import com.fleetify.entity.User;
import com.fleetify.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Companies", description = "Multi-tenant company management. Most endpoints are SUPER_ADMIN only. /profile endpoints are ADMIN only.")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // ─── SUPER_ADMIN: Full CRUD on /api/v1/companies ─────────────────────────

    @GetMapping("/api/v1/companies")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List all companies", description = "Returns all active tenant companies. Requires SUPER_ADMIN.")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("Companies fetched successfully", companies));
    }

    @GetMapping("/api/v1/companies/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get company by ID", description = "Fetch a specific company by UUID. Requires SUPER_ADMIN.")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable UUID id) {
        CompanyResponse company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Company fetched successfully", company));
    }

    @PostMapping("/api/v1/companies")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create company", description = "Onboard a new tenant company. Validates GST and email uniqueness. Requires SUPER_ADMIN.")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse company = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created successfully", company));
    }

    @PutMapping("/api/v1/companies/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update company", description = "Update a tenant company's details. Requires SUPER_ADMIN.")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequest request) {
        CompanyResponse company = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully", company));
    }

    @DeleteMapping("/api/v1/companies/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Soft delete company", description = "Deactivates a company (is_active = false). Requires SUPER_ADMIN.")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted successfully"));
    }

    // ─── ADMIN: Own company profile on /api/v1/company/profile ──────────────

    @GetMapping("/api/v1/company/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER')")
    @Operation(summary = "Get my company profile", description = "Returns the authenticated user's own company profile. Accessible by ADMIN and FLEET_MANAGER.")
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompanyProfile(
            @AuthenticationPrincipal User currentUser) {
        CompanyResponse company = companyService.getCompanyById(currentUser.getCompany().getId());
        return ResponseEntity.ok(ApiResponse.success("Company profile fetched successfully", company));
    }

    @PutMapping("/api/v1/company/profile")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update my company profile", description = "Allows an ADMIN to update their own company's details (name, GST, address, logo, etc.).")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateMyCompanyProfile(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal User currentUser) {
        CompanyResponse company = companyService.updateCompany(currentUser.getCompany().getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Company profile updated successfully", company));
    }
}
