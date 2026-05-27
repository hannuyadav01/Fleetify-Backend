package com.fleetify.scheduler;

import com.fleetify.entity.*;
import com.fleetify.enums.*;
import com.fleetify.repository.*;
import com.fleetify.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class AlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertScheduler.class);

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final DriverRepository driverRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AlertScheduler(VehicleDocumentRepository vehicleDocumentRepository,
                          DriverRepository driverRepository,
                          MaintenanceRecordRepository maintenanceRecordRepository,
                          AlertRepository alertRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.vehicleDocumentRepository = vehicleDocumentRepository;
        this.driverRepository = driverRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Daily Cron job at 1:00 AM to scan compliance expiries and raise alerts.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void runDailyComplianceScan() {
        log.info("Starting daily fleet compliance scan...");
        
        scanVehicleDocuments();
        scanDriverLicenses();
        scanMaintenanceSchedules();
        
        log.info("Daily fleet compliance scan completed successfully.");
    }

    private void scanVehicleDocuments() {
        LocalDate scanLimit = LocalDate.now().plusDays(30);
        List<VehicleDocument> expiringDocs = vehicleDocumentRepository.findAllExpiringOnOrBefore(scanLimit);

        for (VehicleDocument doc : expiringDocs) {
            // Check if unresolved alert already exists for this document
            if (alertRepository.existsByDocumentIdAndIsResolvedFalse(doc.getId())) {
                continue;
            }

            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), doc.getExpiryDate());
            AlertSeverity severity = determineSeverity(daysBetween);
            AlertType alertType = mapDocTypeToAlertType(doc.getDocType());

            Alert alert = new Alert();
            alert.setCompany(doc.getCompany());
            alert.setVehicle(doc.getVehicle());
            alert.setDocument(doc);
            alert.setAlertType(alertType);
            alert.setSeverity(severity);
            alert.setDueDate(doc.getExpiryDate());
            
            String msg = String.format("%s compliance document for vehicle %s expires in %d days (%s)",
                    doc.getDocType().name(), doc.getVehicle().getVehicleNumber(), daysBetween, doc.getExpiryDate());
            if (daysBetween <= 0) {
                msg = String.format("%s compliance document for vehicle %s EXPIRED on %s",
                        doc.getDocType().name(), doc.getVehicle().getVehicleNumber(), doc.getExpiryDate());
            }
            alert.setMessage(msg);

            alertRepository.save(alert);
            notifyAdmins(doc.getCompany().getId(), "Vehicle Compliance Alert", msg);
        }
    }

    private void scanDriverLicenses() {
        LocalDate scanLimit = LocalDate.now().plusDays(30);
        List<Driver> activeDrivers = driverRepository.findAll();

        for (Driver driver : activeDrivers) {
            if (!driver.isActive() || driver.getLicenseExpiry() == null) {
                continue;
            }

            LocalDate expiry = driver.getLicenseExpiry();
            if (expiry.isBefore(scanLimit) || expiry.isEqual(scanLimit)) {
                if (alertRepository.existsByDriverIdAndAlertTypeAndIsResolvedFalse(driver.getId(), AlertType.LICENSE_EXPIRY)) {
                    continue;
                }

                long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
                AlertSeverity severity = determineSeverity(daysBetween);

                Alert alert = new Alert();
                alert.setCompany(driver.getCompany());
                alert.setDriver(driver);
                alert.setAlertType(AlertType.LICENSE_EXPIRY);
                alert.setSeverity(severity);
                alert.setDueDate(expiry);

                String msg = String.format("Driver %s license (%s) expires in %d days (%s)",
                        driver.getFullName(), driver.getLicenseNumber(), daysBetween, expiry);
                if (daysBetween <= 0) {
                    msg = String.format("Driver %s license (%s) EXPIRED on %s",
                            driver.getFullName(), driver.getLicenseNumber(), expiry);
                }
                alert.setMessage(msg);

                alertRepository.save(alert);
                notifyAdmins(driver.getCompany().getId(), "Driver License Alert", msg);
            }
        }
    }

    private void scanMaintenanceSchedules() {
        LocalDate scanLimit = LocalDate.now().plusDays(7);
        List<MaintenanceRecord> dueRecords = maintenanceRecordRepository.findAllDueOnOrBefore(scanLimit);

        for (MaintenanceRecord record : dueRecords) {
            if (record.getNextServiceDate() == null) {
                continue;
            }

            // Check if unresolved alert already exists for this vehicle service
            if (alertRepository.existsByVehicleIdAndAlertTypeAndIsResolvedFalse(record.getVehicle().getId(), AlertType.SERVICE_DUE)) {
                continue;
            }

            Alert alert = new Alert();
            alert.setCompany(record.getCompany());
            alert.setVehicle(record.getVehicle());
            alert.setAlertType(AlertType.SERVICE_DUE);
            alert.setSeverity(AlertSeverity.HIGH);
            alert.setDueDate(record.getNextServiceDate());

            String msg = String.format("Maintenance Service (%s) is due for vehicle %s on %s",
                    record.getServiceType().name(), record.getVehicle().getVehicleNumber(), record.getNextServiceDate());
            alert.setMessage(msg);

            alertRepository.save(alert);
            notifyAdmins(record.getCompany().getId(), "Vehicle Maintenance Alert", msg);
        }
    }

    private AlertSeverity determineSeverity(long daysRemaining) {
        if (daysRemaining <= 0) {
            return AlertSeverity.CRITICAL;
        } else if (daysRemaining <= 7) {
            return AlertSeverity.HIGH;
        } else if (daysRemaining <= 15) {
            return AlertSeverity.MEDIUM;
        } else {
            return AlertSeverity.LOW;
        }
    }

    private AlertType mapDocTypeToAlertType(DocumentType type) {
        switch (type) {
            case INSURANCE:
                return AlertType.INSURANCE_EXPIRY;
            case FITNESS:
                return AlertType.FITNESS_EXPIRY;
            case PERMIT:
                return AlertType.PERMIT_EXPIRY;
            case POLLUTION:
                return AlertType.POLLUTION_EXPIRY;
            default:
                return AlertType.FITNESS_EXPIRY;
        }
    }

    private void notifyAdmins(UUID companyId, String title, String msg) {
        List<User> admins = userRepository.findAllByCompanyIdAndRoleAndIsActiveTrue(companyId, Role.ADMIN);
        for (User admin : admins) {
            if (admin.getFcmToken() != null && !admin.getFcmToken().isBlank()) {
                notificationService.sendPushNotification(admin.getFcmToken(), title, msg);
            }
        }
    }
}
