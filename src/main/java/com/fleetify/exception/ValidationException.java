package com.fleetify.exception;

// Thrown when incoming request data fails business rules (not @Valid annotation failures —
// those are handled separately via MethodArgumentNotValidException in GlobalExceptionHandler).
// Example: assigning a driver who is already ON_TRIP, or setting balance_due on a PAID trip.
// Maps to HTTP 400 Bad Request in GlobalExceptionHandler.
public class ValidationException extends RuntimeException {

    private final String errorCode;

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
