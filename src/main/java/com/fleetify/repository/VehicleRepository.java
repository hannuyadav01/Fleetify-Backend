package com.fleetify.repository;

import com.fleetify.entity.Vehicle;
import com.fleetify.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findAllByCompanyIdAndIsActiveTrue(UUID companyId);
    List<Vehicle> findAllByCompanyIdAndStatus(UUID companyId, VehicleStatus status);
    Optional<Vehicle> findByIdAndCompanyIdAndIsActiveTrue(UUID id, UUID companyId);
    boolean existsByVehicleNumber(String vehicleNumber);
}
