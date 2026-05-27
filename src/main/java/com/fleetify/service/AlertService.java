package com.fleetify.service;

import com.fleetify.dto.response.AlertResponse;
import com.fleetify.entity.Alert;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getUnresolvedAlerts(UUID companyId) {
        List<Alert> alerts = (companyId == null)
                ? alertRepository.findAll().stream().filter(a -> !a.isResolved()).collect(Collectors.toList())
                : alertRepository.findAllByCompanyIdAndIsResolvedFalseOrderByCreatedAtDesc(companyId);
        return alerts.stream().map(AlertResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts(UUID companyId) {
        List<Alert> alerts = (companyId == null)
                ? alertRepository.findAll()
                : alertRepository.findAllByCompanyId(companyId);
        return alerts.stream().map(AlertResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public AlertResponse resolveAlert(UUID id, UUID companyId) {
        Alert alert;
        if (companyId == null) {
            alert = alertRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        } else {
            alert = alertRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
            if (!alert.getCompany().getId().equals(companyId)) {
                throw new ResourceNotFoundException("Alert", "id", id);
            }
        }

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert = alertRepository.save(alert);
        return AlertResponse.from(alert);
    }
}
