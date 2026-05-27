package com.fleetify.repository;

import com.fleetify.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByInvoiceId(UUID invoiceId);
    List<Payment> findAllByInvoiceCompanyId(UUID companyId);
    Optional<Payment> findByIdAndInvoiceCompanyId(UUID id, UUID companyId);
}
