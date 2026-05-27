package com.fleetify.service;

import com.fleetify.dto.request.DriverRequest;
import com.fleetify.dto.response.DriverResponse;
import com.fleetify.entity.Company;
import com.fleetify.entity.Driver;
import com.fleetify.entity.User;
import com.fleetify.enums.DriverStatus;
import com.fleetify.enums.Role;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.repository.CompanyRepository;
import com.fleetify.repository.DriverRepository;
import com.fleetify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository,
                         CompanyRepository companyRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── Get All Drivers for a company ─────────────────────────────

    @Transactional(readOnly = true)
    public List<DriverResponse> getAllDrivers(UUID companyId) {
        return driverRepository.findAllByCompanyIdAndIsActiveTrue(companyId).stream()
                .map(DriverResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Get All Drivers by status ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<DriverResponse> getDriversByStatus(UUID companyId, DriverStatus status) {
        return driverRepository.findAllByCompanyIdAndStatus(companyId, status).stream()
                .map(DriverResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Get single driver ─────────────────────────────────────────

    @Transactional(readOnly = true)
    public DriverResponse getDriverById(UUID id, UUID companyId) {
        Driver driver;
        if (companyId == null) {
            // SUPER_ADMIN: fetch without tenant filter
            driver = driverRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        } else {
            driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        }
        return DriverResponse.from(driver);
    }

    // ─── Create Driver (also creates their linked User account) ────

    @Transactional
    public DriverResponse createDriver(DriverRequest request, UUID companyId) {
        // Duplicate checks
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntryException("User with email '" + request.getEmail() + "' already exists");
        }
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateEntryException("Driver with license number '" + request.getLicenseNumber() + "' already exists");
        }
        if (driverRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateEntryException("Driver with phone '" + request.getPhone() + "' already exists");
        }

        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // 1. Create the linked User account for mobile app login
        User driverUser = new User();
        driverUser.setFullName(request.getFullName());
        driverUser.setEmail(request.getEmail());
        driverUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        driverUser.setRole(Role.DRIVER);
        driverUser.setCompany(company);
        driverUser.setActive(true);
        driverUser = userRepository.save(driverUser);

        // 2. Create the Driver profile linked to that User
        Driver driver = new Driver();
        driver.setCompany(company);
        driver.setUser(driverUser);
        driver.setFullName(request.getFullName());
        driver.setPhone(request.getPhone());
        driver.setEmergencyContact(request.getEmergencyContact());
        driver.setAddress(request.getAddress());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setJoiningDate(request.getJoiningDate());
        driver.setMonthlySalary(request.getMonthlySalary());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setLicenseType(request.getLicenseType());
        driver.setAadhaarNumber(request.getAadhaarNumber());
        driver.setPanNumber(request.getPanNumber());
        driver.setStatus(request.getStatus() != null ? request.getStatus() : DriverStatus.AVAILABLE);
        driver.setProfilePhotoUrl(request.getProfilePhotoUrl());
        driver.setActive(true);

        driver = driverRepository.save(driver);
        return DriverResponse.from(driver);
    }

    // ─── Update Driver ─────────────────────────────────────────────

    @Transactional
    public DriverResponse updateDriver(UUID id, DriverRequest request, UUID companyId) {
        Driver driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));

        // Check phone uniqueness only if changed
        if (!request.getPhone().equals(driver.getPhone()) && driverRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateEntryException("Driver with phone '" + request.getPhone() + "' already exists");
        }
        // Check license uniqueness only if changed
        if (!request.getLicenseNumber().equals(driver.getLicenseNumber())
                && driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateEntryException("Driver with license number '" + request.getLicenseNumber() + "' already exists");
        }

        driver.setFullName(request.getFullName());
        driver.setPhone(request.getPhone());
        driver.setEmergencyContact(request.getEmergencyContact());
        driver.setAddress(request.getAddress());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setJoiningDate(request.getJoiningDate());
        driver.setMonthlySalary(request.getMonthlySalary());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setLicenseType(request.getLicenseType());
        driver.setAadhaarNumber(request.getAadhaarNumber());
        driver.setPanNumber(request.getPanNumber());
        if (request.getStatus() != null) {
            driver.setStatus(request.getStatus());
        }
        driver.setProfilePhotoUrl(request.getProfilePhotoUrl());

        // Also keep the linked User's name in sync
        if (driver.getUser() != null) {
            driver.getUser().setFullName(request.getFullName());
            userRepository.save(driver.getUser());
        }

        driver = driverRepository.save(driver);
        return DriverResponse.from(driver);
    }

    // ─── Soft Delete Driver ────────────────────────────────────────

    @Transactional
    public void deleteDriver(UUID id, UUID companyId) {
        Driver driver;
        if (companyId == null) {
            driver = driverRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        } else {
            driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        }
        driver.setActive(false);
        // Also deactivate the linked user login
        if (driver.getUser() != null) {
            driver.getUser().setActive(false);
            userRepository.save(driver.getUser());
        }
        driverRepository.save(driver);
    }
}
