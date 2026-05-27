package com.fleetify.controller;

import com.fleetify.dto.request.VehicleRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.VehicleResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicles", description = "Fleet vehicle asset management. FLEET_MANAGER or ADMIN required for writes.")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "List all vehicles", description = "Returns all active vehicles for the company. Any authenticated user can read.")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {

        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched successfully", vehicles));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Fetch a single vehicle's details. Enforces tenant boundary.")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        VehicleResponse vehicle = vehicleService.getVehicleById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle fetched successfully", vehicle));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Create vehicle", description = "Add a new vehicle to the fleet. Requires FLEET_MANAGER or ADMIN role. Validates vehicle number uniqueness.")
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        VehicleResponse vehicle = vehicleService.createVehicle(request, targetCompanyId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle created successfully", vehicle));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Update vehicle", description = "Update a vehicle's details. Requires FLEET_MANAGER or ADMIN role.")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        VehicleResponse vehicle = vehicleService.updateVehicle(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", vehicle));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Soft delete vehicle", description = "Sets is_active = false on the vehicle. Only ADMIN or SUPER_ADMIN can delete.")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        vehicleService.deleteVehicle(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }

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
