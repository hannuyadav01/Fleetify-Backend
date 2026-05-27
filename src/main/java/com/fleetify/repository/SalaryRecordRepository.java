package com.fleetify.repository;

import com.fleetify.entity.SalaryRecord;
import com.fleetify.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, UUID> {
    List<SalaryRecord> findAllByCompanyId(UUID companyId);
    List<SalaryRecord> findAllByDriverId(UUID driverId);
    List<SalaryRecord> findAllByCompanyIdAndPaymentStatus(UUID companyId, PaymentStatus paymentStatus);

    // Prevent duplicate salary records for the same driver/month/year
    Optional<SalaryRecord> findByDriverIdAndMonthAndYear(UUID driverId, int month, int year);
    boolean existsByDriverIdAndMonthAndYear(UUID driverId, int month, int year);
}
