package com.fleetify.controller;

import com.fleetify.dto.request.*;
import com.fleetify.dto.response.*;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    // Helper: resolve and lock down the company scope
    private UUID resolveCompanyId(User currentUser, UUID requestedCompanyId) {
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            if (requestedCompanyId == null) {
                throw new ValidationException("companyId is required for SUPER_ADMIN actions");
            }
            return requestedCompanyId;
        }
        return currentUser.getCompany().getId();
    }

    // ─────────────────────────────────────────────────────────────
    // EXPENSE ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/expenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<ExpenseResponse> response = financeService.getAllExpenses(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", response));
    }

    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        ExpenseResponse response = financeService.getExpenseById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", response));
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'DRIVER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        ExpenseResponse response = financeService.createExpense(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense created successfully", response));
    }

    @PutMapping("/expenses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        ExpenseResponse response = financeService.updateExpense(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    @PostMapping("/expenses/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> verifyExpense(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        ExpenseResponse response = financeService.verifyExpense(id, companyId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Expense verified successfully", response));
    }

    @DeleteMapping("/expenses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deleteExpense(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    // ─────────────────────────────────────────────────────────────
    // FUEL LOG ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/fuel-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FuelLogResponse>>> getAllFuelLogs(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<FuelLogResponse> response = financeService.getAllFuelLogs(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel logs fetched successfully", response));
    }

    @GetMapping("/fuel-logs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FuelLogResponse>> getFuelLogById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        FuelLogResponse response = financeService.getFuelLogById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel log fetched successfully", response));
    }

    @PostMapping("/fuel-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'DRIVER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FuelLogResponse>> createFuelLog(
            @Valid @RequestBody FuelLogRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        FuelLogResponse response = financeService.createFuelLog(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fuel log created successfully", response));
    }

    @PutMapping("/fuel-logs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FuelLogResponse>> updateFuelLog(
            @PathVariable UUID id,
            @Valid @RequestBody FuelLogRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        FuelLogResponse response = financeService.updateFuelLog(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel log updated successfully", response));
    }

    @DeleteMapping("/fuel-logs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFuelLog(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deleteFuelLog(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel log deleted successfully"));
    }

    // ─────────────────────────────────────────────────────────────
    // INVOICE ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<InvoiceResponse> response = financeService.getAllInvoices(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Invoices fetched successfully", response));
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        InvoiceResponse response = financeService.getInvoiceById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Invoice fetched successfully", response));
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        InvoiceResponse response = financeService.createInvoice(request, targetCompanyId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created successfully", response));
    }

    @PutMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> updateInvoice(
            @PathVariable UUID id,
            @Valid @RequestBody InvoiceRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        InvoiceResponse response = financeService.updateInvoice(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Invoice updated successfully", response));
    }

    @DeleteMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deleteInvoice(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully"));
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<PaymentResponse> response = financeService.getAllPayments(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Payments fetched successfully", response));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        PaymentResponse response = financeService.getPaymentById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Payment fetched successfully", response));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        PaymentResponse response = financeService.createPayment(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment registered successfully", response));
    }

    @PutMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePayment(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        PaymentResponse response = financeService.updatePayment(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Payment updated successfully", response));
    }

    @DeleteMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deletePayment(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully"));
    }

    // ─────────────────────────────────────────────────────────────
    // SALARY RECORD ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/salaries")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<SalaryRecordResponse>>> getAllSalaryRecords(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<SalaryRecordResponse> response = financeService.getAllSalaryRecords(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Salary records fetched successfully", response));
    }

    @GetMapping("/salaries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SalaryRecordResponse>> getSalaryRecordById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        SalaryRecordResponse response = financeService.getSalaryRecordById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Salary record fetched successfully", response));
    }

    @PostMapping("/salaries")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SalaryRecordResponse>> createSalaryRecord(
            @Valid @RequestBody SalaryRecordRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        SalaryRecordResponse response = financeService.createSalaryRecord(request, targetCompanyId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary record created successfully", response));
    }

    @PutMapping("/salaries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SalaryRecordResponse>> updateSalaryRecord(
            @PathVariable UUID id,
            @Valid @RequestBody SalaryRecordRequest request,
            @AuthenticationPrincipal User currentUser) {
        UUID targetCompanyId = resolveCompanyId(currentUser, request.getCompanyId());
        SalaryRecordResponse response = financeService.updateSalaryRecord(id, request, targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Salary record updated successfully", response));
    }

    @DeleteMapping("/salaries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryRecord(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deleteSalaryRecord(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Salary record deleted successfully"));
    }
}
