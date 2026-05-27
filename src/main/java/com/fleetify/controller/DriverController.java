package com.fleetify.controller;

import com.fleetify.dto.request.DriverRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.DriverAvailabilityResponse;
import com.fleetify.dto.response.DriverResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.DriverStatus;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.DriverService;
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
@RequestMapping("/api/v1/drivers")
@Tag(name = "Drivers", description = "Driver HR management. Creating a driver also creates their login account via @Transactional.")
@SecurityRequirement(name = "bearerAuth")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // GET /api/v1/drivers
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "List all drivers", description = "Returns all active drivers for the company. Optionally filter by status (AVAILABLE, ON_TRIP, ON_LEAVE).")
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
    @Operation(summary = "Get driver by ID", description = "Fetch a single driver's full profile. Enforces tenant boundary.")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        DriverResponse driver = driverService.getDriverById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Driver fetched successfully", driver));
    }

    // GET /api/v1/drivers/{id}/availability
    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Check driver availability", description = "Returns the driver's current status and a boolean 'available' flag (true only when status = AVAILABLE).")
    public ResponseEntity<ApiResponse<DriverAvailabilityResponse>> getDriverAvailability(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        DriverAvailabilityResponse availability = driverService.getDriverAvailability(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Driver availability fetched successfully", availability));
    }

    // POST /api/v1/drivers
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create driver", description = "Creates a Driver profile AND a linked User login account in the same @Transactional. Both rows are created or neither.")
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
    @Operation(summary = "Update driver", description = "Updates the driver profile. Also syncs the linked user's full name. Validates license and phone uniqueness on change.")
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
    @Operation(summary = "Soft delete driver", description = "Deactivates the driver profile AND their linked user login account (is_active = false). Data is preserved.")
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
