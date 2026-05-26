package com.fleetify.entity;

import com.fleetify.enums.DriverDocumentType;
import jakarta.persistence.*;

import java.time.LocalDate;

// Identity and compliance documents for a driver (Aadhaar, PAN, License, Photo, Police Verification).
@Entity
@Table(name = "driver_documents")
public class DriverDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // AADHAAR, PAN, DRIVING_LICENSE, PHOTO, POLICE_VERIFICATION, OTHER
    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private DriverDocumentType docType;

    // S3 / Cloudflare R2 URL of the uploaded file
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    // Aadhaar number, PAN, License number etc.
    @Column(name = "document_number")
    private String documentNumber;

    // Nullable — only License and Police Verification have expiry dates
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // False when a newer upload supersedes this document
    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    // ─── Getters & Setters ───────────────────────────────────────

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public DriverDocumentType getDocType() { return docType; }
    public void setDocType(DriverDocumentType docType) { this.docType = docType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}
