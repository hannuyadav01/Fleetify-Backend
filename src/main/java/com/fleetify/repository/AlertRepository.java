package com.fleetify.repository;

import com.fleetify.entity.Alert;
import com.fleetify.enums.AlertSeverity;
import com.fleetify.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    // Unresolved alerts for a company dashboard
    List<Alert> findAllByCompanyIdAndIsResolvedFalseOrderByCreatedAtDesc(UUID companyId);
    List<Alert> findAllByCompanyId(UUID companyId);
    List<Alert> findAllByCompanyIdAndSeverity(UUID companyId, AlertSeverity severity);
    List<Alert> findAllByVehicleId(UUID vehicleId);
    List<Alert> findAllByDriverId(UUID driverId);
    boolean existsByDocumentIdAndIsResolvedFalse(UUID documentId);
    boolean existsByDriverIdAndAlertTypeAndIsResolvedFalse(UUID driverId, AlertType alertType);
    boolean existsByVehicleIdAndAlertTypeAndIsResolvedFalse(UUID vehicleId, AlertType alertType);
}
