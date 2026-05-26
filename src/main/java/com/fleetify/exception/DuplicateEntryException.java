package com.fleetify.exception;

// Thrown when a unique constraint would be violated — e.g. duplicate vehicle_number,
// duplicate email, duplicate license_number, duplicate tracking_code.
// Maps to HTTP 409 Conflict in GlobalExceptionHandler.
public class DuplicateEntryException extends RuntimeException {

    private final String errorCode;

    public DuplicateEntryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // Convenience constructor — auto-builds message like "Vehicle with vehicle_number 'HR55AB1234' already exists"
    public DuplicateEntryException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " with " + fieldName + " '" + fieldValue + "' already exists");
        this.errorCode = "DUPLICATE_" + resourceName.toUpperCase().replace(" ", "_");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
