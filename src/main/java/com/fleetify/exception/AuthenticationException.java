package com.fleetify.exception;

// Thrown specifically for authentication failures — wrong password, inactive user, suspended company.
// Separate from Spring Security's AuthenticationException so we can return our own JSON shape.
// Maps to HTTP 401 Unauthorized in GlobalExceptionHandler.
public class AuthenticationException extends RuntimeException {

    private final String errorCode;

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTHENTICATION_FAILED";
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
