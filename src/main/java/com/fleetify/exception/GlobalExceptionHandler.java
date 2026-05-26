package com.fleetify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Central exception handler for the entire Fleetify API.
// Every exception thrown anywhere in the app is caught here and converted
// into a clean, consistent JSON error response — no raw Spring error pages.
//
// Handler priority (top to bottom):
//   1. Domain exceptions (ResourceNotFound, Validation, Duplicate, Auth, AccessDenied)
//   2. Spring MVC validation (@Valid failures on DTOs)
//   3. Catch-all for any unexpected exception
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────────────
    // 404 — Resource does not exist
    // Example: GET /api/vehicles/{id} where id doesn't exist
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 400 — Business rule / domain validation failed
    // Example: Assigning a driver who is already ON_TRIP
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex) {
        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 409 — Unique constraint violated
    // Example: Creating a vehicle with a vehicle_number that already exists
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 401 — Authentication failed (wrong password, inactive user, suspended company)
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex) {
        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 403 — Cross-tenant data access attempt or insufficient role
    // Example: A DRIVER trying to view another company's vehicles
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 400 — @Valid annotation failure on request DTOs
    // Collects all field-level errors into a map:
    // { "email": "must not be blank", "vehicleNumber": "must match '[A-Z]{2}[0-9]...'" }
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        ApiErrorResponse response = new ApiErrorResponse(
                "Request validation failed. Check the 'errors' field for details.",
                "VALIDATION_FAILED",
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ─────────────────────────────────────────────────────────────
    // 500 — Unexpected server error (catch-all)
    // Logs the real error internally but returns a safe, non-leaking message.
    // ─────────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        // TODO: integrate Sentry/Logtail error reporting here in Phase 4
        ApiErrorResponse response = new ApiErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
