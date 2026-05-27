package com.fleetify.repository;

import com.fleetify.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, UUID> {
    List<MaintenanceRecord> findAllByCompanyId(UUID companyId);
    List<MaintenanceRecord> findAllByVehicleId(UUID vehicleId);
    Optional<MaintenanceRecord> findByIdAndCompanyId(UUID id, UUID companyId);

    // Find records with next service due on or before a date — used by alert scheduler
    @Query("SELECT m FROM MaintenanceRecord m WHERE m.nextServiceDate <= :date")
    List<MaintenanceRecord> findAllDueOnOrBefore(LocalDate date);
}
