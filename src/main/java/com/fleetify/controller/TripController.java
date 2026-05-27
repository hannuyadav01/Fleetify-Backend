package com.fleetify.controller;

import com.fleetify.dto.request.TripRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.TripResponse;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.enums.TripStatus;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.TripService;
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
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // GET /api/v1/trips
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'ACCOUNTANT', 'SUPER_ADMIN')")
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

    // GET /api/v1/trips/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'ACCOUNTANT', 'SUPER_ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        TripResponse trip = tripService.getTripById(id, companyId);

        // Security check: Drivers can only view their own assigned trips
        if (currentUser.getRole() == Role.DRIVER) {
            // Find driver profile linked to current user
            if (trip.getDriverId() == null || !trip.getDriverName().equals(currentUser.getFullName())) {
                throw new org.springframework.security.access.AccessDeniedException("Access Denied: Drivers can only view their own trips");
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Trip fetched successfully", trip));
    }

    // POST /api/v1/trips
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        TripResponse trip = tripService.createTrip(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", trip));
    }

    // PUT /api/v1/trips/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @PathVariable UUID id,
            @Valid @RequestBody TripRequest request,
            @AuthenticationPrincipal User currentUser) {

        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        TripResponse trip = tripService.updateTrip(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", trip));
    }

    // DELETE /api/v1/trips/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN
                ? null
                : currentUser.getCompany().getId();

        tripService.deleteTrip(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully"));
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
