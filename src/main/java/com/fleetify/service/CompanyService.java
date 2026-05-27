package com.fleetify.service;

import com.fleetify.dto.request.CompanyRequest;
import com.fleetify.dto.response.CompanyResponse;
import com.fleetify.entity.Company;
import com.fleetify.enums.CompanyStatus;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAllByIsActiveTrue().stream()
                .map(CompanyResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(UUID id) {
        Company company = companyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return CompanyResponse.from(company);
    }

    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        // Validate unique email if provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (companyRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEntryException("Company", "email", request.getEmail());
            }
        }

        // Validate unique GST number if provided
        if (request.getGstNumber() != null && !request.getGstNumber().isBlank()) {
            if (companyRepository.existsByGstNumber(request.getGstNumber())) {
                throw new DuplicateEntryException("Company", "gstNumber", request.getGstNumber());
            }
        }

        Company company = new Company();
        company.setName(request.getName());
        company.setGstNumber(request.getGstNumber());
        company.setPanNumber(request.getPanNumber());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLogoUrl(request.getLogoUrl());
        company.setStatus(request.getStatus() != null ? request.getStatus() : CompanyStatus.TRIAL);
        company.setActive(true);

        company = companyRepository.save(company);
        return CompanyResponse.from(company);
    }

    @Transactional
    public CompanyResponse updateCompany(UUID id, CompanyRequest request) {
        Company company = companyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        // Validate unique email if changed
        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equals(company.getEmail())) {
            if (companyRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEntryException("Company", "email", request.getEmail());
            }
        }

        // Validate unique GST if changed
        if (request.getGstNumber() != null && !request.getGstNumber().isBlank() && !request.getGstNumber().equals(company.getGstNumber())) {
            if (companyRepository.existsByGstNumber(request.getGstNumber())) {
                throw new DuplicateEntryException("Company", "gstNumber", request.getGstNumber());
            }
        }

        company.setName(request.getName());
        company.setGstNumber(request.getGstNumber());
        company.setPanNumber(request.getPanNumber());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setLogoUrl(request.getLogoUrl());
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
        }

        company = companyRepository.save(company);
        return CompanyResponse.from(company);
    }

    @Transactional
    public void deleteCompany(UUID id) {
        Company company = companyRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        company.setActive(false);
        companyRepository.save(company);
    }
}
