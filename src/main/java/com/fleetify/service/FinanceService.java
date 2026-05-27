package com.fleetify.service;

import com.fleetify.dto.request.*;
import com.fleetify.dto.response.*;
import com.fleetify.entity.*;
import com.fleetify.enums.*;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.exception.ValidationException;
import com.fleetify.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FinanceService {

    private final ExpenseRepository expenseRepository;
    private final FuelLogRepository fuelLogRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    
    private final CompanyRepository companyRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public FinanceService(ExpenseRepository expenseRepository,
                          FuelLogRepository fuelLogRepository,
                          InvoiceRepository invoiceRepository,
                          PaymentRepository paymentRepository,
                          SalaryRecordRepository salaryRecordRepository,
                          CompanyRepository companyRepository,
                          TripRepository tripRepository,
                          VehicleRepository vehicleRepository,
                          DriverRepository driverRepository,
                          UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.fuelLogRepository = fuelLogRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.companyRepository = companyRepository;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    // ─────────────────────────────────────────────────────────────
    // EXPENSE OPERATIONS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses(UUID companyId) {
        List<Expense> expenses = (companyId == null)
                ? expenseRepository.findAll()
                : expenseRepository.findAllByCompanyId(companyId);
        return expenses.stream().map(ExpenseResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(UUID id, UUID companyId) {
        Expense expense = (companyId == null)
                ? expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id))
                : expenseRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request, UUID companyId, User currentUser) {
        if (request.getTripId() == null && request.getVehicleId() == null) {
            throw new ValidationException("Either tripId or vehicleId must be provided for an expense");
        }

        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));
        }

        Vehicle vehicle = null;
        if (request.getVehicleId() != null) {
            vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));
        }

        Expense expense = new Expense();
        expense.setCompany(company);
        expense.setTrip(trip);
        expense.setVehicle(vehicle);
        expense.setExpenseType(request.getExpenseType());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setReceiptUrl(request.getReceiptUrl());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCreatedBy(currentUser);
        expense.setVerified(false);

        expense = expenseRepository.save(expense);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request, UUID companyId) {
        if (request.getTripId() == null && request.getVehicleId() == null) {
            throw new ValidationException("Either tripId or vehicleId must be provided for an expense");
        }

        Expense expense = expenseRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));
        }

        Vehicle vehicle = null;
        if (request.getVehicleId() != null) {
            vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));
        }

        expense.setTrip(trip);
        expense.setVehicle(vehicle);
        expense.setExpenseType(request.getExpenseType());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setReceiptUrl(request.getReceiptUrl());
        expense.setExpenseDate(request.getExpenseDate());

        expense = expenseRepository.save(expense);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse verifyExpense(UUID id, UUID companyId, User currentUser) {
        Expense expense = expenseRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        expense.setVerified(true);
        expense.setVerifiedBy(currentUser);
        expense.setVerifiedAt(LocalDateTime.now());

        expense = expenseRepository.save(expense);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void deleteExpense(UUID id, UUID companyId) {
        Expense expense = (companyId == null)
                ? expenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id))
                : expenseRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        expenseRepository.delete(expense);
    }

    // ─────────────────────────────────────────────────────────────
    // FUEL LOG OPERATIONS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FuelLogResponse> getAllFuelLogs(UUID companyId) {
        List<FuelLog> logs = (companyId == null)
                ? fuelLogRepository.findAll()
                : fuelLogRepository.findAllByCompanyId(companyId);
        return logs.stream().map(FuelLogResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuelLogResponse getFuelLogById(UUID id, UUID companyId) {
        FuelLog log = (companyId == null)
                ? fuelLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FuelLog", "id", id))
                : fuelLogRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("FuelLog", "id", id));
        return FuelLogResponse.from(log);
    }

    @Transactional
    public FuelLogResponse createFuelLog(FuelLogRequest request, UUID companyId, User currentUser) {
        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));
        }

        FuelLog log = new FuelLog();
        log.setCompany(company);
        log.setVehicle(vehicle);
        log.setTrip(trip);
        log.setQuantityLitres(request.getQuantityLitres());
        log.setCostPerLitre(request.getCostPerLitre());
        
        // Auto compute total cost if null or recalculate for accuracy
        BigDecimal calculatedTotal = request.getQuantityLitres().multiply(request.getCostPerLitre());
        log.setTotalCost(request.getTotalCost() != null ? request.getTotalCost() : calculatedTotal);
        
        log.setFuelStation(request.getFuelStation());
        log.setOdometerAtFill(request.getOdometerAtFill());
        log.setReceiptUrl(request.getReceiptUrl());
        log.setFilledAt(request.getFilledAt());
        log.setFilledBy(currentUser);

        // Optional: Update vehicle's current odometer if this fill odometer is higher
        if (request.getOdometerAtFill() > vehicle.getCurrentOdometerKm()) {
            vehicle.setCurrentOdometerKm(request.getOdometerAtFill());
            vehicleRepository.save(vehicle);
        }

        log = fuelLogRepository.save(log);
        return FuelLogResponse.from(log);
    }

    @Transactional
    public FuelLogResponse updateFuelLog(UUID id, FuelLogRequest request, UUID companyId) {
        FuelLog log = fuelLogRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("FuelLog", "id", id));

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));
        }

        log.setVehicle(vehicle);
        log.setTrip(trip);
        log.setQuantityLitres(request.getQuantityLitres());
        log.setCostPerLitre(request.getCostPerLitre());
        
        BigDecimal calculatedTotal = request.getQuantityLitres().multiply(request.getCostPerLitre());
        log.setTotalCost(request.getTotalCost() != null ? request.getTotalCost() : calculatedTotal);

        log.setFuelStation(request.getFuelStation());
        log.setOdometerAtFill(request.getOdometerAtFill());
        log.setReceiptUrl(request.getReceiptUrl());
        log.setFilledAt(request.getFilledAt());

        if (request.getOdometerAtFill() > vehicle.getCurrentOdometerKm()) {
            vehicle.setCurrentOdometerKm(request.getOdometerAtFill());
            vehicleRepository.save(vehicle);
        }

        log = fuelLogRepository.save(log);
        return FuelLogResponse.from(log);
    }

    @Transactional
    public void deleteFuelLog(UUID id, UUID companyId) {
        FuelLog log = (companyId == null)
                ? fuelLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FuelLog", "id", id))
                : fuelLogRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("FuelLog", "id", id));
        fuelLogRepository.delete(log);
    }

    // ─────────────────────────────────────────────────────────────
    // INVOICE OPERATIONS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices(UUID companyId) {
        List<Invoice> invoices = (companyId == null)
                ? invoiceRepository.findAll()
                : invoiceRepository.findAllByCompanyId(companyId);
        return invoices.stream().map(InvoiceResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(UUID id, UUID companyId) {
        Invoice invoice = (companyId == null)
                ? invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id))
                : invoiceRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request, UUID companyId) {
        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Trip trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        // Ensure 1-to-1 trip check
        if (invoiceRepository.findByTripId(request.getTripId()).isPresent()) {
            throw new DuplicateEntryException("An invoice already exists for Trip id '" + request.getTripId() + "'");
        }

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCustomerId()));
        }

        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setTrip(trip);
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(request.getStatus() != null ? request.getStatus() : InvoiceStatus.DRAFT);
        invoice.setNotes(request.getNotes());

        // Compute GST details
        BigDecimal subtotal = request.getSubtotal();
        BigDecimal rate = request.getGstRate();
        BigDecimal gstAmount = subtotal.multiply(rate.divide(BigDecimal.valueOf(100)));
        BigDecimal total = subtotal.add(gstAmount);

        invoice.setSubtotal(subtotal);
        invoice.setGstRate(rate);
        invoice.setGstAmount(gstAmount);
        invoice.setTotalAmount(total);

        // Generate / Validate Invoice number
        if (request.getInvoiceNumber() == null || request.getInvoiceNumber().isBlank()) {
            String invNo;
            do {
                invNo = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (invoiceRepository.existsByInvoiceNumber(invNo));
            invoice.setInvoiceNumber(invNo);
        } else {
            if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
                throw new DuplicateEntryException("Invoice", "invoiceNumber", request.getInvoiceNumber());
            }
            invoice.setInvoiceNumber(request.getInvoiceNumber());
        }

        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public InvoiceResponse updateInvoice(UUID id, InvoiceRequest request, UUID companyId) {
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        Trip trip = tripRepository.findByIdAndCompanyId(request.getTripId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        // If trip changed, check uniqueness
        if (!invoice.getTrip().getId().equals(request.getTripId())) {
            if (invoiceRepository.findByTripId(request.getTripId()).isPresent()) {
                throw new DuplicateEntryException("An invoice already exists for Trip id '" + request.getTripId() + "'");
            }
        }

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCustomerId()));
        }

        invoice.setTrip(trip);
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            invoice.setStatus(request.getStatus());
        }
        invoice.setNotes(request.getNotes());

        // Recompute GST details
        BigDecimal subtotal = request.getSubtotal();
        BigDecimal rate = request.getGstRate();
        BigDecimal gstAmount = subtotal.multiply(rate.divide(BigDecimal.valueOf(100)));
        BigDecimal total = subtotal.add(gstAmount);

        invoice.setSubtotal(subtotal);
        invoice.setGstRate(rate);
        invoice.setGstAmount(gstAmount);
        invoice.setTotalAmount(total);

        // Validate Invoice number if changed
        if (request.getInvoiceNumber() != null && !request.getInvoiceNumber().isBlank()) {
            if (!request.getInvoiceNumber().equals(invoice.getInvoiceNumber())) {
                if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
                    throw new DuplicateEntryException("Invoice", "invoiceNumber", request.getInvoiceNumber());
                }
                invoice.setInvoiceNumber(request.getInvoiceNumber());
            }
        }

        invoice = invoiceRepository.save(invoice);
        
        // Recalculate trip payment values if invoice has payments
        syncTripPayments(invoice.getTrip());

        return InvoiceResponse.from(invoice);
    }

    @Transactional
    public void deleteInvoice(UUID id, UUID companyId) {
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        invoiceRepository.delete(invoice);
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT OPERATIONS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments(UUID companyId) {
        List<Payment> payments = (companyId == null)
                ? paymentRepository.findAll()
                : paymentRepository.findAllByInvoiceCompanyId(companyId);
        return payments.stream().map(PaymentResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id, UUID companyId) {
        Payment payment = (companyId == null)
                ? paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id))
                : paymentRepository.findByIdAndInvoiceCompanyId(id, companyId).orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, UUID companyId, User currentUser) {
        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Invoice invoice = invoiceRepository.findByIdAndCompanyId(request.getInvoiceId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        Payment payment = new Payment();
        payment.setCompany(company);
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setNotes(request.getNotes());
        payment.setReceivedBy(currentUser);

        payment = paymentRepository.save(payment);

        // Reactively update Invoice status and Trip payments
        syncInvoiceAndTripPaymentState(invoice);

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse updatePayment(UUID id, PaymentRequest request, UUID companyId) {
        Payment payment = paymentRepository.findByIdAndInvoiceCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        Invoice invoice = invoiceRepository.findByIdAndCompanyId(request.getInvoiceId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        Invoice oldInvoice = payment.getInvoice();

        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setNotes(request.getNotes());

        payment = paymentRepository.save(payment);

        // Sync old invoice state
        syncInvoiceAndTripPaymentState(oldInvoice);
        // Sync new invoice state if changed
        if (!oldInvoice.getId().equals(invoice.getId())) {
            syncInvoiceAndTripPaymentState(invoice);
        }

        return PaymentResponse.from(payment);
    }

    @Transactional
    public void deletePayment(UUID id, UUID companyId) {
        Payment payment = paymentRepository.findByIdAndInvoiceCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        Invoice invoice = payment.getInvoice();
        
        paymentRepository.delete(payment);

        // Sync state after deleting the payment
        syncInvoiceAndTripPaymentState(invoice);
    }

    // Helper: Syncs the payment states
    private void syncInvoiceAndTripPaymentState(Invoice invoice) {
        List<Payment> payments = paymentRepository.findAllByInvoiceId(invoice.getId());
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 1. Update Invoice Status
        if (invoice.getTotalAmount() != null) {
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                // If it's partially paid, we could set SENT or PAID, let's keep status SENT or update accordingly.
                // Standard: if paid < total amount, it remains SENT (unpaid/partial in accounting) or we can leave as is.
                if (invoice.getStatus() == InvoiceStatus.DRAFT) {
                    invoice.setStatus(InvoiceStatus.SENT);
                }
            }
            invoiceRepository.save(invoice);
        }

        // 2. Update associated Trip payment statuses
        if (invoice.getTrip() != null) {
            syncTripPayments(invoice.getTrip());
        }
    }

    private void syncTripPayments(Trip trip) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findByTripId(trip.getId());
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            List<Payment> payments = paymentRepository.findAllByInvoiceId(invoice.getId());
            BigDecimal invoicePayments = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal freight = trip.getFreightAmount() != null ? trip.getFreightAmount() : BigDecimal.ZERO;
            BigDecimal advance = trip.getAdvancePaid() != null ? trip.getAdvancePaid() : BigDecimal.ZERO;

            // Total payments credited = advance paid + invoice payments
            BigDecimal totalCredited = advance.add(invoicePayments);
            BigDecimal balance = freight.subtract(totalCredited);

            trip.setBalanceDue(balance);

            if (freight.compareTo(BigDecimal.ZERO) == 0) {
                trip.setPaymentStatus(PaymentStatus.PAID);
            } else if (totalCredited.compareTo(BigDecimal.ZERO) == 0) {
                trip.setPaymentStatus(PaymentStatus.PENDING);
            } else if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                trip.setPaymentStatus(PaymentStatus.PAID);
            } else {
                trip.setPaymentStatus(PaymentStatus.PARTIAL);
            }
            tripRepository.save(trip);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SALARY RECORD OPERATIONS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SalaryRecordResponse> getAllSalaryRecords(UUID companyId) {
        List<SalaryRecord> records = (companyId == null)
                ? salaryRecordRepository.findAll()
                : salaryRecordRepository.findAllByCompanyId(companyId);
        return records.stream().map(SalaryRecordResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalaryRecordResponse getSalaryRecordById(UUID id, UUID companyId) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));
        if (companyId != null && !record.getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("SalaryRecord", "id", id);
        }
        return SalaryRecordResponse.from(record);
    }

    @Transactional
    public SalaryRecordResponse createSalaryRecord(SalaryRecordRequest request, UUID companyId, User currentUser) {
        // Prevent duplicate records for same driver/month/year
        if (salaryRecordRepository.existsByDriverIdAndMonthAndYear(request.getDriverId(), request.getMonth(), request.getYear())) {
            throw new DuplicateEntryException("Salary record for driver already exists for " + request.getMonth() + "/" + request.getYear());
        }

        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Driver driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getDriverId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        SalaryRecord record = new SalaryRecord();
        record.setCompany(company);
        record.setDriver(driver);
        record.setMonth(request.getMonth());
        record.setYear(request.getYear());
        record.setBaseSalary(request.getBaseSalary());

        BigDecimal allowances = request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO;
        BigDecimal net = request.getBaseSalary().add(allowances).subtract(deductions);

        record.setAllowances(allowances);
        record.setDeductions(deductions);
        record.setNetSalary(net);
        record.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING);
        record.setPaymentDate(request.getPaymentDate());
        record.setPaidBy(currentUser);
        record.setNotes(request.getNotes());

        record = salaryRecordRepository.save(record);
        return SalaryRecordResponse.from(record);
    }

    @Transactional
    public SalaryRecordResponse updateSalaryRecord(UUID id, SalaryRecordRequest request, UUID companyId) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));
        if (companyId != null && !record.getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("SalaryRecord", "id", id);
        }

        // Validate unique constraint on update if driver/month/year changed
        if (!record.getDriver().getId().equals(request.getDriverId()) ||
                !record.getMonth().equals(request.getMonth()) ||
                !record.getYear().equals(request.getYear())) {
            if (salaryRecordRepository.existsByDriverIdAndMonthAndYear(request.getDriverId(), request.getMonth(), request.getYear())) {
                throw new DuplicateEntryException("Salary record for driver already exists for " + request.getMonth() + "/" + request.getYear());
            }
        }

        Driver driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getDriverId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        record.setDriver(driver);
        record.setMonth(request.getMonth());
        record.setYear(request.getYear());
        record.setBaseSalary(request.getBaseSalary());

        BigDecimal allowances = request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO;
        BigDecimal net = request.getBaseSalary().add(allowances).subtract(deductions);

        record.setAllowances(allowances);
        record.setDeductions(deductions);
        record.setNetSalary(net);
        if (request.getPaymentStatus() != null) {
            record.setPaymentStatus(request.getPaymentStatus());
        }
        record.setPaymentDate(request.getPaymentDate());
        record.setNotes(request.getNotes());

        record = salaryRecordRepository.save(record);
        return SalaryRecordResponse.from(record);
    }

    @Transactional
    public void deleteSalaryRecord(UUID id, UUID companyId) {
        SalaryRecord record = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));
        if (companyId != null && !record.getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("SalaryRecord", "id", id);
        }
        salaryRecordRepository.delete(record);
    }
}
