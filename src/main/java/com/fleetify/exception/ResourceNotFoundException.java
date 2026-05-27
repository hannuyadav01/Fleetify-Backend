package com.fleetify.exception;

// Thrown when a requested resource (vehicle, driver, trip etc.) does not exist in the DB.
// Maps to HTTP 404 Not Found in GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    // Convenience constructor — auto-builds message like "Vehicle not found with id: <id>"
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + ": " + fieldValue);
        this.errorCode = resourceName.toUpperCase().replace(" ", "_") + "_NOT_FOUND";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
