package com.fleetify.repository;

import com.fleetify.entity.Invoice;
import com.fleetify.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findAllByCompanyId(UUID companyId);
    List<Invoice> findAllByCompanyIdAndStatus(UUID companyId, InvoiceStatus status);
    Optional<Invoice> findByTripId(UUID tripId);
    Optional<Invoice> findByIdAndCompanyId(UUID id, UUID companyId);
    List<Invoice> findAllByCompanyIdAndInvoiceDateBetween(UUID companyId, LocalDate from, LocalDate to);
    boolean existsByInvoiceNumber(String invoiceNumber);
}
