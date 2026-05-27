package com.fleetify.repository;

import com.fleetify.entity.Expense;
import com.fleetify.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findAllByCompanyId(UUID companyId);
    List<Expense> findAllByCompanyIdAndTripId(UUID companyId, UUID tripId);
    List<Expense> findAllByCompanyIdAndVehicleId(UUID companyId, UUID vehicleId);
    List<Expense> findAllByCompanyIdAndExpenseType(UUID companyId, ExpenseType type);
    List<Expense> findAllByCompanyIdAndExpenseDateBetween(UUID companyId, LocalDate from, LocalDate to);
    Optional<Expense> findByIdAndCompanyId(UUID id, UUID companyId);
}
