package com.fleetify.repository;

import com.fleetify.entity.VehicleDocument;
import com.fleetify.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, UUID> {
    List<VehicleDocument> findAllByVehicleIdAndIsCurrentTrue(UUID vehicleId);
    List<VehicleDocument> findAllByVehicleCompanyId(UUID companyId);
    List<VehicleDocument> findAllByDocumentType(DocumentType documentType);

    // Find docs expiring on or before a date — used by alert scheduler
    @Query("SELECT vd FROM VehicleDocument vd WHERE vd.expiryDate <= :date AND vd.isCurrent = true")
    List<VehicleDocument> findAllExpiringOnOrBefore(LocalDate date);
}
