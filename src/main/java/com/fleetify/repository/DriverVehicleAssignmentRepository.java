package com.fleetify.repository;

import com.fleetify.entity.DriverVehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverVehicleAssignmentRepository extends JpaRepository<DriverVehicleAssignment, UUID> {
    List<DriverVehicleAssignment> findAllByCompanyId(UUID companyId);
    List<DriverVehicleAssignment> findAllByDriverId(UUID driverId);
    List<DriverVehicleAssignment> findAllByVehicleId(UUID vehicleId);

    // Check if a driver or vehicle is currently assigned (endDate is null)
    Optional<DriverVehicleAssignment> findByDriverIdAndEndDateIsNull(UUID driverId);
    Optional<DriverVehicleAssignment> findByVehicleIdAndEndDateIsNull(UUID vehicleId);
}
