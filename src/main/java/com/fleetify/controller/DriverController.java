package com.fleetify.controller;

import com.fleetify.dto.request.DriverRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.DriverResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.DriverStatus;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // GET /api/v1/drivers
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<DriverResponse>>> getAllDrivers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) DriverStatus status) {

        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);

        List<DriverResponse> drivers = (status != null)
                ? driverService.getDriversByStatus(targetCompanyId, status)
                : driverService.getAllDrivers(targetCompanyId);

        return ResponseEntity.ok(ApiResponse.success("Drivers fetched successfully", drivers));
    }

    // GET /api/v1/drivers/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        DriverResponse driver = driverService.getDriverById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Driver fetched successfully", driver));
    }

    // POST /api/v1/drivers
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DriverResponse>> createDriver(
            @Valid @RequestBody DriverRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        DriverResponse driver = driverService.createDriver(request, targetCompanyId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Driver created successfully", driver));
    }

    // PUT /api/v1/drivers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(
            @PathVariable UUID id,
            @Valid @RequestBody DriverRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        DriverResponse driver = driverService.updateDriver(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", driver));
    }

    // DELETE /api/v1/drivers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        driverService.deleteDriver(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully"));
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
