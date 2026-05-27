package com.fleetify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

// Standard JSON shape for ALL successful responses in Fleetify API.
// Every controller method wraps its return value in this class.
//
// Success shape:
// {
//   "success": true,
//   "message": "Vehicle created successfully",
//   "data": { ... },
//   "timestamp": "2026-05-26T10:00:00"
// }
//
// Usage in controllers:
//   return ResponseEntity.ok(ApiResponse.success("Vehicle fetched", vehicleDto));
//   return ResponseEntity.status(201).body(ApiResponse.success("Vehicle created", vehicleDto));
//   return ResponseEntity.ok(ApiResponse.success("Vehicle deleted"));
@JsonInclude(JsonInclude.Include.NON_NULL) // omits "data" field when null (e.g. delete operations)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // Private constructor — use static factory methods below
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ─────────────────────────────────────────────────────────────
    // Static factory methods — used by every controller
    // ─────────────────────────────────────────────────────────────

    // For responses WITH data (GET, POST, PUT)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // For responses WITHOUT data (DELETE, status updates)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ─────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
