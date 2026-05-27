package com.fleetify.controller;

import com.fleetify.dto.request.LoginRequest;
import com.fleetify.dto.request.RegisterRequest;
import com.fleetify.dto.response.ApiResponse;
import com.fleetify.dto.response.AuthResponse;
import com.fleetify.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    // POST /api/v1/auth/register
    // Typically called by SUPER_ADMIN to create company admins, or by ADMIN to add staff.
    // In production this should be @PreAuthorize-protected; left open here for initial setup.
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }
}
