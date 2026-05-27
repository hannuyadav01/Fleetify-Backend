package com.fleetify.repository;

import com.fleetify.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FuelLogRepository extends JpaRepository<FuelLog, UUID> {
    List<FuelLog> findAllByCompanyId(UUID companyId);
    List<FuelLog> findAllByVehicleId(UUID vehicleId);
    List<FuelLog> findAllByFilledById(UUID userId);
    List<FuelLog> findAllByCompanyIdAndFilledAtBetween(UUID companyId, LocalDateTime from, LocalDateTime to);
    Optional<FuelLog> findByIdAndCompanyId(UUID id, UUID companyId);
}
