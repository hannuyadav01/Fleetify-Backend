package com.fleetify.controller;

import com.fleetify.dto.request.*;
import com.fleetify.dto.response.*;
import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import com.fleetify.exception.ValidationException;
import com.fleetify.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Finance", description = "Financial records: Expenses, Fuel Logs, GST Invoices, Customer Payments and Salary Records.")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "List expenses", description = "Returns all operating expenses for the company. Accessible by ADMIN and ACCOUNTANT.")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<ExpenseResponse> response = financeService.getAllExpenses(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", response));
    }

    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "Get expense by ID", description = "Fetch a single expense record.")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        ExpenseResponse response = financeService.getExpenseById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", response));
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'DRIVER', 'SUPER_ADMIN')")
    @Operation(summary = "Create expense", description = "Log a new operating expense (toll, fuel, loading, etc.). Drivers can submit their own expense slips.")
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
    @Operation(summary = "Update expense", description = "Update an existing expense record. ACCOUNTANT or ADMIN only.")
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
    @Operation(summary = "Verify expense", description = "Mark a driver-submitted expense as verified by an ACCOUNTANT or ADMIN.")
    public ResponseEntity<ApiResponse<ExpenseResponse>> verifyExpense(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        ExpenseResponse response = financeService.verifyExpense(id, companyId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Expense verified successfully", response));
    }

    @DeleteMapping("/expenses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete expense", description = "Soft delete an expense record. ADMIN only.")
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
    @Operation(summary = "List fuel logs", description = "Returns all fuel fill records. Used for mileage computation and fuel cost tracking.")
    public ResponseEntity<ApiResponse<List<FuelLogResponse>>> getAllFuelLogs(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<FuelLogResponse> response = financeService.getAllFuelLogs(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel logs fetched successfully", response));
    }

    @GetMapping("/fuel-logs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'FLEET_MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Get fuel log by ID", description = "Fetch a single fuel log entry.")
    public ResponseEntity<ApiResponse<FuelLogResponse>> getFuelLogById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        FuelLogResponse response = financeService.getFuelLogById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Fuel log fetched successfully", response));
    }

    @PostMapping("/fuel-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'FLEET_MANAGER', 'DRIVER', 'SUPER_ADMIN')")
    @Operation(summary = "Create fuel log", description = "Log a fuel fill-up with odometer readings. Drivers can submit their own slips.")
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
    @Operation(summary = "Update fuel log", description = "Update a fuel log entry. FLEET_MANAGER or ADMIN only.")
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
    @Operation(summary = "Delete fuel log", description = "Soft delete a fuel log. ADMIN only.")
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
    @Operation(summary = "List invoices", description = "Returns all GST invoices raised against trips. ACCOUNTANT or ADMIN only.")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<InvoiceResponse> response = financeService.getAllInvoices(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Invoices fetched successfully", response));
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "Get invoice by ID", description = "Fetch a single invoice.")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        InvoiceResponse response = financeService.getInvoiceById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Invoice fetched successfully", response));
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "Create invoice", description = "Generate a GST invoice for a completed trip. Requires ACCOUNTANT or ADMIN.")
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
    @Operation(summary = "Update invoice", description = "Update invoice details such as tax rate or amount.")
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
    @Operation(summary = "Delete invoice", description = "Soft delete an invoice. ADMIN only.")
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
    @Operation(summary = "List payments", description = "Returns all incoming customer payments against invoices.")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<PaymentResponse> response = financeService.getAllPayments(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Payments fetched successfully", response));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "Get payment by ID", description = "Fetch a single payment record.")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        PaymentResponse response = financeService.getPaymentById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Payment fetched successfully", response));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'SUPER_ADMIN')")
    @Operation(summary = "Record payment", description = "Record an incoming customer payment (advance, partial, or full settlement) against an invoice.")
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
    @Operation(summary = "Update payment", description = "Correct a payment record. ACCOUNTANT or ADMIN only.")
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
    @Operation(summary = "Delete payment", description = "Soft delete a payment record. ADMIN only.")
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
    @Operation(summary = "List salary records", description = "Returns all payroll snapshots for drivers. ADMIN only — confidential payroll data.")
    public ResponseEntity<ApiResponse<List<SalaryRecordResponse>>> getAllSalaryRecords(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID companyId) {
        UUID targetCompanyId = resolveCompanyId(currentUser, companyId);
        List<SalaryRecordResponse> response = financeService.getAllSalaryRecords(targetCompanyId);
        return ResponseEntity.ok(ApiResponse.success("Salary records fetched successfully", response));
    }

    @GetMapping("/salaries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get salary record by ID", description = "Fetch a single monthly payroll snapshot.")
    public ResponseEntity<ApiResponse<SalaryRecordResponse>> getSalaryRecordById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        SalaryRecordResponse response = financeService.getSalaryRecordById(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Salary record fetched successfully", response));
    }

    @PostMapping("/salaries")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create salary record", description = "Generate a monthly salary snapshot for a driver including deductions and net pay.")
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
    @Operation(summary = "Update salary record", description = "Update a salary record, e.g. to correct deductions or bonus amounts.")
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
    @Operation(summary = "Delete salary record", description = "Soft delete a salary record. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryRecord(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID companyId = currentUser.getRole() == Role.SUPER_ADMIN ? null : currentUser.getCompany().getId();
        financeService.deleteSalaryRecord(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Salary record deleted successfully"));
    }
}
