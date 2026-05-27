package com.fleetify.repository;

import com.fleetify.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocument, UUID> {
    List<DriverDocument> findAllByDriverIdAndIsCurrentTrue(UUID driverId);
    List<DriverDocument> findAllByDriverCompanyId(UUID companyId);

    // Find docs expiring on or before a date — used by alert scheduler
    @Query("SELECT dd FROM DriverDocument dd WHERE dd.expiryDate <= :date AND dd.isCurrent = true")
    List<DriverDocument> findAllExpiringOnOrBefore(LocalDate date);
}
