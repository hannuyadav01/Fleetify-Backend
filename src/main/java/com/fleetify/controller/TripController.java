package com.fleetify.controller;

import com.fleetify.dto.request.TripRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.TripResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.enums.TripStatus;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.TripService;
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
@RequestMapping("/api/v1/trips")
@Tag(name = "Trips", description = "Freight trip management — routes, scheduling, tracking codes, and financial computation.")
@SecurityRequirement(name = "bearerAuth")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "List all trips", description = "Returns all trips for the company. Filter by status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED).")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getAllTrips(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) TripStatus status) {

        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);

        List<TripResponse> trips = (status != null)
                ? tripService.getTripsByStatus(targetCompanyId, status)
                : tripService.getAllTrips(targetCompanyId);

        return ResponseEntity.ok(ApiResponse.success("Trips fetched successfully", trips));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'ACCOUNTANT', 'SUPER_ADMIN', 'DRIVER')")
    @Operation(summary = "Get trip by ID", description = "Fetch full trip details. Drivers can only access their own assigned trips.")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        TripResponse trip = tripService.getTripById(id, companyId);

        if (currentUser.getRole() == Role.DRIVER) {
            if (trip.getDriverId() == null || !trip.getDriverName().equals(currentUser.getFullName())) {
                throw new org.springframework.security.access.AccessDeniedException("Access Denied: Drivers can only view their own trips");
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Trip fetched successfully", trip));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Create trip", description = "Plan a new freight trip. Auto-generates a tracking code and computes balance due from freight and advance amounts. Requires FLEET_MANAGER or ADMIN.")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        TripResponse trip = tripService.createTrip(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", trip));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Update trip", description = "Update trip details including status transitions (e.g. PLANNED → IN_PROGRESS). Requires FLEET_MANAGER or ADMIN.")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @PathVariable UUID id,
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        TripResponse trip = tripService.updateTrip(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", trip));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Soft delete trip", description = "Deactivates a trip record (is_active = false). Only ADMIN or SUPER_ADMIN can delete.")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        tripService.deleteTrip(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully"));
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
