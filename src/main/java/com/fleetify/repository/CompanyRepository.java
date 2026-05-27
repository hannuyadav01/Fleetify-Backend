package com.fleetify.repository;

import com.fleetify.entity.Company;
import com.fleetify.enums.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByIdAndIsActiveTrue(UUID id);
    List<Company> findAllByIsActiveTrue();
    List<Company> findAllByStatus(CompanyStatus status);
    boolean existsByEmail(String email);
    boolean existsByGstNumber(String gstNumber);
}
