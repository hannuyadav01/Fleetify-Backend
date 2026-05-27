package com.fleetify.controller;

import com.fleetify.dto.request.VehicleRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.VehicleResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.VehicleService;
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
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // GET /api/v1/vehicles
    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched successfully", vehicles));
    }

    // GET /api/v1/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        VehicleResponse vehicle = vehicleService.getVehicleById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle fetched successfully", vehicle));
    }

    // POST /api/v1/vehicles
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        VehicleResponse vehicle = vehicleService.createVehicle(request, targetCompanyId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle created successfully", vehicle));
    }

    // PUT /api/v1/vehicles/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        VehicleResponse vehicle = vehicleService.updateVehicle(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", vehicle));
    }

    // DELETE /api/v1/vehicles/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();
                
        vehicleService.deleteVehicle(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }

    // Helper to resolve and secure the company ID context
    private UUID resolveCompanyId(User currentUser, UUID requestedCompanyId) {
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            if (requestedCompanyId == null) {
                throw new ValidationException("companyId is required for SUPER_ADMIN actions");
            }
            return requestedCompanyId;
        }
        // Force the tenant context of the logged-in user
        return currentUser.getCompany().getId();
    }
}
