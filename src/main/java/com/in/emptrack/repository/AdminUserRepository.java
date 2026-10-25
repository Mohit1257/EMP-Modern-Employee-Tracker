package com.in.emptrack.repository;

import com.in.emptrack.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByEmailIgnoreCase(String email);
    boolean existsByUsername(String username);
    boolean existsByRole(String role);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
