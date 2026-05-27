package com.fleetify.entity;

import com.fleetify.enums.DocumentType;
import jakarta.persistence.*;

import java.time.LocalDate;

// Compliance documents for a vehicle: RC, Insurance, Fitness, Permit, Pollution.
// The alert scheduler scans expiryDate daily to create EXPIRY alerts.
@Entity
@Table(name = "vehicle_documents")
public class VehicleDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Denormalised company_id for fast multi-tenant filtering
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // RC, INSURANCE, FITNESS, PERMIT, POLLUTION
    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private DocumentType docType;

    // Insurance policy number, RC chassis number, etc.
    @Column(name = "document_number", length = 100)
    private String documentNumber;

    // RTO, ARTO, Transport Dept etc.
    @Column(name = "issuing_authority")
    private String issuingAuthority;

    // S3 / Cloudflare R2 URL of the scanned document
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    // Drives alert generation — alerts fire at 90/30/7/0 days before this date
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    // False when a newer document of the same type has been uploaded (superseded)
    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    // Who uploaded this document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    // ─── Getters & Setters ───────────────────────────────────────

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public DocumentType getDocType() { return docType; }
    public void setDocType(DocumentType docType) { this.docType = docType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}
