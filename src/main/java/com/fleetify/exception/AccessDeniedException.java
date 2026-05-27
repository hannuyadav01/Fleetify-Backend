package com.fleetify.exception;

// Thrown when a user tries to access data that belongs to a different company_id.
// This is a hard security boundary — the service layer throws this when
// the authenticated user's companyId does not match the requested resource's companyId.
// Maps to HTTP 403 Forbidden in GlobalExceptionHandler.
public class AccessDeniedException extends RuntimeException {

    private final String errorCode;

    public AccessDeniedException(String message) {
        super(message);
        this.errorCode = "ACCESS_DENIED";
    }

    public AccessDeniedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
