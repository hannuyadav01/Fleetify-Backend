package com.fleetify.controller;

import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.AlertResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // GET /api/v1/alerts
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAlerts(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "false") boolean all) {

        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);

        List<AlertResponse> alerts = all 
                ? alertService.getAllAlerts(targetCompanyId)
                : alertService.getUnresolvedAlerts(targetCompanyId);

        return ResponseEntity.ok(ApiResponse.success("Alerts fetched successfully", alerts));
    }

    // POST /api/v1/alerts/{id}/resolve
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AlertResponse>> resolveAlert(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        AlertResponse response = alertService.resolveAlert(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Alert resolved successfully", response));
    }

    // Helper: resolve and lock down the company scope
    private UUID resolveCompanyId(User currentUser, UUID requestedCompanyId) {
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            if (requestedCompanyId == null) {
                throw new ValidationException("companyId is required for SUPER_ADMIN actions");
            }
            return requestedCompanyId;
        }
        return currentUser.getCompany().getId();
    }
}
