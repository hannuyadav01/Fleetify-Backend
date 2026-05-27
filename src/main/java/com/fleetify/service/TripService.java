package com.fleetify.service;

import com.fleetify.dto.request.TripRequest;
import com.fleetify.dto.response.TripResponse;
import com.fleetify.entity.*;
import com.fleetify.enums.PaymentStatus;
import com.fleetify.enums.TripStatus;
import com.fleetify.exception.DuplicateEntryException;
import com.fleetify.exception.ResourceNotFoundException;
import com.fleetify.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final DriverVehicleAssignmentRepository driverVehicleAssignmentRepository;

    public TripService(TripRepository tripRepository,
                       CompanyRepository companyRepository,
                       VehicleRepository vehicleRepository,
                       DriverRepository driverRepository,
                       UserRepository userRepository,
                       DriverVehicleAssignmentRepository driverVehicleAssignmentRepository) {
        this.tripRepository = tripRepository;
        this.companyRepository = companyRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.driverVehicleAssignmentRepository = driverVehicleAssignmentRepository;
    }

    // ─── Get All Trips ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips(UUID companyId) {
        List<Trip> trips;
        if (companyId == null) {
            trips = tripRepository.findAll();
        } else {
            trips = tripRepository.findAllByCompanyId(companyId);
        }
        return trips.stream()
                .map(TripResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Get Trips By Status ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TripResponse> getTripsByStatus(UUID companyId, TripStatus status) {
        List<Trip> trips;
        if (companyId == null) {
            trips = tripRepository.findAll().stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        } else {
            trips = tripRepository.findAllByCompanyIdAndStatus(companyId, status);
        }
        return trips.stream()
                .map(TripResponse::from)
                .collect(Collectors.toList());
    }

    // ─── Get Single Trip ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public TripResponse getTripById(UUID id, UUID companyId) {
        Trip trip;
        if (companyId == null) {
            trip = tripRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        } else {
            trip = tripRepository.findByIdAndCompanyId(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        }
        return TripResponse.from(trip);
    }

    // ─── Create Trip ───────────────────────────────────────────────

    @Transactional
    public TripResponse createTrip(TripRequest request, UUID companyId, User currentUser) {
        Company company = companyRepository.findByIdAndIsActiveTrue(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Driver driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getDriverId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCustomerId()));
        }

        Trip trip = new Trip();
        trip.setCompany(company);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setCustomer(customer);
        trip.setSource(request.getSource());
        trip.setDestination(request.getDestination());
        trip.setRouteDetails(request.getRouteDetails());
        trip.setDistanceKm(request.getDistanceKm());
        trip.setScheduledStart(request.getScheduledStart());
        trip.setActualStart(request.getActualStart());
        trip.setScheduledEnd(request.getScheduledEnd());
        trip.setActualEnd(request.getActualEnd());
        trip.setStatus(request.getStatus() != null ? request.getStatus() : TripStatus.SCHEDULED);
        trip.setGoodsType(request.getGoodsType());
        trip.setWeightTons(request.getWeightTons());
        trip.setNotes(request.getNotes());
        trip.setCreatedBy(currentUser);

        // Track and compute financial balances
        BigDecimal freight = request.getFreightAmount() != null ? request.getFreightAmount() : BigDecimal.ZERO;
        BigDecimal advance = request.getAdvancePaid() != null ? request.getAdvancePaid() : BigDecimal.ZERO;
        BigDecimal balance = freight.subtract(advance);

        trip.setFreightAmount(freight);
        trip.setAdvancePaid(advance);
        trip.setBalanceDue(balance);

        // Auto determine PaymentStatus
        if (freight.compareTo(BigDecimal.ZERO) == 0) {
            trip.setPaymentStatus(PaymentStatus.PAID);
        } else if (advance.compareTo(BigDecimal.ZERO) == 0) {
            trip.setPaymentStatus(PaymentStatus.PENDING);
        } else if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            trip.setPaymentStatus(PaymentStatus.PAID);
        } else {
            trip.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        // Set assignment context automatically if active
        Optional<DriverVehicleAssignment> assignmentOpt = driverVehicleAssignmentRepository
                .findByDriverIdAndEndDateIsNull(driver.getId());
        if (assignmentOpt.isPresent() && assignmentOpt.get().getVehicle().getId().equals(vehicle.getId())) {
            trip.setAssignment(assignmentOpt.get());
        }

        // Generate / Validate tracking code
        if (request.getTrackingCode() == null || request.getTrackingCode().isBlank()) {
            String trackingCode;
            do {
                trackingCode = "TRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            } while (tripRepository.existsByTrackingCode(trackingCode));
            trip.setTrackingCode(trackingCode);
        } else {
            if (tripRepository.existsByTrackingCode(request.getTrackingCode())) {
                throw new DuplicateEntryException("Trip with tracking code '" + request.getTrackingCode() + "' already exists");
            }
            trip.setTrackingCode(request.getTrackingCode());
        }

        trip = tripRepository.save(trip);
        return TripResponse.from(trip);
    }

    // ─── Update Trip ───────────────────────────────────────────────

    @Transactional
    public TripResponse updateTrip(UUID id, TripRequest request, UUID companyId) {
        Trip trip = tripRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getVehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Driver driver = driverRepository.findByIdAndCompanyIdAndIsActiveTrue(request.getDriverId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCustomerId()));
        }

        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setCustomer(customer);
        trip.setSource(request.getSource());
        trip.setDestination(request.getDestination());
        trip.setRouteDetails(request.getRouteDetails());
        trip.setDistanceKm(request.getDistanceKm());
        trip.setScheduledStart(request.getScheduledStart());
        trip.setActualStart(request.getActualStart());
        trip.setScheduledEnd(request.getScheduledEnd());
        trip.setActualEnd(request.getActualEnd());
        if (request.getStatus() != null) {
            trip.setStatus(request.getStatus());
        }
        trip.setGoodsType(request.getGoodsType());
        trip.setWeightTons(request.getWeightTons());
        trip.setNotes(request.getNotes());

        // Update financial calculations
        BigDecimal freight = request.getFreightAmount() != null ? request.getFreightAmount() : BigDecimal.ZERO;
        BigDecimal advance = request.getAdvancePaid() != null ? request.getAdvancePaid() : BigDecimal.ZERO;
        BigDecimal balance = freight.subtract(advance);

        trip.setFreightAmount(freight);
        trip.setAdvancePaid(advance);
        trip.setBalanceDue(balance);

        // Auto determine PaymentStatus
        if (freight.compareTo(BigDecimal.ZERO) == 0) {
            trip.setPaymentStatus(PaymentStatus.PAID);
        } else if (advance.compareTo(BigDecimal.ZERO) == 0) {
            trip.setPaymentStatus(PaymentStatus.PENDING);
        } else if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            trip.setPaymentStatus(PaymentStatus.PAID);
        } else {
            trip.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        // Set assignment context automatically if active
        Optional<DriverVehicleAssignment> assignmentOpt = driverVehicleAssignmentRepository
                .findByDriverIdAndEndDateIsNull(driver.getId());
        if (assignmentOpt.isPresent() && assignmentOpt.get().getVehicle().getId().equals(vehicle.getId())) {
            trip.setAssignment(assignmentOpt.get());
        } else {
            trip.setAssignment(null);
        }

        // Validate unique tracking code if changed
        if (request.getTrackingCode() != null && !request.getTrackingCode().isBlank()) {
            if (!request.getTrackingCode().equals(trip.getTrackingCode())) {
                if (tripRepository.existsByTrackingCode(request.getTrackingCode())) {
                    throw new DuplicateEntryException("Trip with tracking code '" + request.getTrackingCode() + "' already exists");
                }
                trip.setTrackingCode(request.getTrackingCode());
            }
        }

        trip = tripRepository.save(trip);
        return TripResponse.from(trip);
    }

    // ─── Delete Trip ───────────────────────────────────────────────

    @Transactional
    public void deleteTrip(UUID id, UUID companyId) {
        Trip trip;
        if (companyId == null) {
            trip = tripRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        } else {
            trip = tripRepository.findByIdAndCompanyId(id, companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        }
        tripRepository.delete(trip);
    }
}
