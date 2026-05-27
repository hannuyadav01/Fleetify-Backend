package com.fleetify.repository;

import com.fleetify.entity.Driver;
import com.fleetify.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findAllByCompanyIdAndIsActiveTrue(UUID companyId);
    List<Driver> findAllByCompanyIdAndStatus(UUID companyId, DriverStatus status);
    Optional<Driver> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    Optional<Driver> findByUserId(UUID userId);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByPhone(String phone);
}
