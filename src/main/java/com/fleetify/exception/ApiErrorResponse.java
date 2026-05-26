package com.fleetify.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

// Standard JSON shape for ALL error responses in Fleetify API.
// Every error goes through GlobalExceptionHandler which builds this object.
//
// Error shape:
// {
//   "success": false,
//   "message": "Vehicle not found with id: abc-123",
//   "error": "VEHICLE_NOT_FOUND",
//   "timestamp": "2026-05-26T10:00:00"
// }
@JsonInclude(JsonInclude.Include.NON_NULL) // omits null fields like "errors" when not present
public class ApiErrorResponse {

    private final boolean success = false;
    private final String message;
    private final String error;
    private final LocalDateTime timestamp;

    // Used for validation errors — contains field-level error details
    private java.util.Map<String, String> errors;

    public ApiErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(String message, String error, java.util.Map<String, String> errors) {
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getError() { return error; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public java.util.Map<String, String> getErrors() { return errors; }
}
