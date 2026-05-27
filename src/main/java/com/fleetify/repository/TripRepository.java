package com.fleetify.repository;

import com.fleetify.entity.Trip;
import com.fleetify.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findAllByCompanyId(UUID companyId);
    List<Trip> findAllByCompanyIdAndStatus(UUID companyId, TripStatus status);
    List<Trip> findAllByDriverId(UUID driverId);
    List<Trip> findAllByVehicleId(UUID vehicleId);
    Optional<Trip> findByIdAndCompanyId(UUID id, UUID companyId);
    List<Trip> findAllByCompanyIdAndScheduledStartBetween(UUID companyId, LocalDateTime from, LocalDateTime to);
    boolean existsByTrackingCode(String trackingCode);
}
