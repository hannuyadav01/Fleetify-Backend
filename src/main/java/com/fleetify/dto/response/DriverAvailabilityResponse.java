package com.fleetify.dto.response;

import com.fleetify.enums.DriverStatus;

import java.util.UUID;

public class DriverAvailabilityResponse {

    private UUID driverId;
    private String fullName;
    private DriverStatus status;
    private boolean available;

    public DriverAvailabilityResponse(UUID driverId, String fullName, DriverStatus status, boolean available) {
        this.driverId = driverId;
        this.fullName = fullName;
        this.status = status;
        this.available = available;
    }

    public UUID getDriverId()     { return driverId; }
    public String getFullName()   { return fullName; }
    public DriverStatus getStatus() { return status; }
    public boolean isAvailable()  { return available; }
}
