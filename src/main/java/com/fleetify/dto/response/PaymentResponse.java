package com.fleetify.dto.response;

import com.fleetify.entity.Payment;
import com.fleetify.enums.PaymentMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {

    private UUID id;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID companyId;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private LocalDate paymentDate;
    private String referenceNumber;
    private String notes;
    private UUID receivedByUserId;
    private String receivedByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.id = payment.getId();
        if (payment.getInvoice() != null) {
            res.invoiceId = payment.getInvoice().getId();
            res.invoiceNumber = payment.getInvoice().getInvoiceNumber();
        }
        res.companyId = payment.getCompany() != null ? payment.getCompany().getId() : null;
        res.amount = payment.getAmount();
        res.paymentMode = payment.getPaymentMode();
        res.paymentDate = payment.getPaymentDate();
        res.referenceNumber = payment.getReferenceNumber();
        res.notes = payment.getNotes();
        if (payment.getReceivedBy() != null) {
            res.receivedByUserId = payment.getReceivedBy().getId();
            res.receivedByUserName = payment.getReceivedBy().getFullName();
        }
        res.createdAt = payment.getCreatedAt();
        res.updatedAt = payment.getUpdatedAt();
        return res;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getInvoiceId() { return invoiceId; }
    public void setInvoiceId(UUID invoiceId) { this.invoiceId = invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public UUID getReceivedByUserId() { return receivedByUserId; }
    public void setReceivedByUserId(UUID receivedByUserId) { this.receivedByUserId = receivedByUserId; }
    public String getReceivedByUserName() { return receivedByUserName; }
    public void setReceivedByUserName(String receivedByUserName) { this.receivedByUserName = receivedByUserName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
