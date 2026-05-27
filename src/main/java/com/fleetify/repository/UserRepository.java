package com.fleetify.repository;

import com.fleetify.entity.User;
import com.fleetify.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Used by Spring Security UserDetailsService for login
    Optional<User> findByEmail(String email);

    // Load all users for a specific company
    List<User> findAllByCompanyIdAndIsActiveTrue(UUID companyId);

    // Load users by role within a company
    List<User> findAllByCompanyIdAndRoleAndIsActiveTrue(UUID companyId, Role role);

    boolean existsByEmail(String email);
}
