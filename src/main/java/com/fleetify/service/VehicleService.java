package com.fleetify.service;

import com.fleetify.dto.request.VehicleRequest;
import com.fleetify.dto.response.VehicleResponse;
import com.fleetify.entity.Company;
import com.fleetify.entity.Vehicle;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.repository.CompanyRepository;
import com.fleetify.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;

    public VehicleService(VehicleRepository vehicleRepository, CompanyRepository companyRepository) {
        this.vehicleRepository = vehicleRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(UUID companyId) {
        return vehicleRepository.findAllByCompanyIdAndIsActiveTrue(companyId).stream()
                .map(VehicleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(UUID id, UUID companyId) {
        Vehicle vehicle;
        if (companyId == null) {
            // SUPER_ADMIN — find by id only, no company filter
            vehicle = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        } else {
            vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        }
        return VehicleResponse.from(vehicle);
    }

    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request, UUID companyId) {
        // Validate unique vehicle number across the platform
        if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new DuplicateEntryException("Vehicle", "vehicleNumber", request.getVehicleNumber());
        }

        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setLoadCapacityTons(request.getLoadCapacityTons());
        vehicle.setCurrentOdometerKm(request.getCurrentOdometerKm() != null ? request.getCurrentOdometerKm() : 0);
        vehicle.setStatus(request.getStatus());
        vehicle.setPurchaseDate(request.getPurchaseDate());
        vehicle.setPurchasePrice(request.getPurchasePrice());
        vehicle.setActive(true);

        vehicle = vehicleRepository.save(vehicle);
        return VehicleResponse.from(vehicle);
    }

    @Transactional
    public VehicleResponse updateVehicle(UUID id, VehicleRequest request, UUID companyId) {
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        // Validate unique vehicle number across the platform if changed
        if (!request.getVehicleNumber().equals(vehicle.getVehicleNumber())) {
            if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
                throw new DuplicateEntryException("Vehicle", "vehicleNumber", request.getVehicleNumber());
            }
        }

        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setLoadCapacityTons(request.getLoadCapacityTons());
        if (request.getCurrentOdometerKm() != null) {
            vehicle.setCurrentOdometerKm(request.getCurrentOdometerKm());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        vehicle.setPurchaseDate(request.getPurchaseDate());
        vehicle.setPurchasePrice(request.getPurchasePrice());

        vehicle = vehicleRepository.save(vehicle);
        return VehicleResponse.from(vehicle);
    }

    @Transactional
    public void deleteVehicle(UUID id, UUID companyId) {
        Vehicle vehicle;
        if (companyId == null) {
            // SUPER_ADMIN — delete by id only, no company filter
            vehicle = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        } else {
            vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        }
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }
}
