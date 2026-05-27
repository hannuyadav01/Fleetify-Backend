package com.fleetify.service;

import com.fleetify.dto.request.LoginRequest;
import com.fleetify.dto.request.RegisterRequest;
import com.fleetify.dto.response.AuthResponse;
import com.fleetify.entity.Company;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.AuthenticationException;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.exception.ValidationException;
import com.fleetify.repository.CompanyRepository;
import com.fleetify.repository.UserRepository;
import com.fleetify.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       CompanyRepository companyRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    // ─── Login ────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) auth.getPrincipal();

            // Update last login timestamp
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtUtils.generateToken(user);

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getCompany() != null ? user.getCompany().getId() : null
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }

    // ─── Register ─────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Prevent duplicate emails across the platform
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntryException("User with email '" + request.getEmail() + "' already exists");
        }

        // All roles except SUPER_ADMIN must belong to a company
        Company company = null;
        if (request.getRole() != Role.SUPER_ADMIN) {
            if (request.getCompanyId() == null) {
                throw new ValidationException("companyId is required for role: " + request.getRole());
            }
            company = companyRepository.findByIdAndIsActiveTrue(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setCompany(company);
        user.setActive(true);

        user = userRepository.save(user);

        String token = jwtUtils.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                company != null ? company.getId() : null
        );
    }
}
