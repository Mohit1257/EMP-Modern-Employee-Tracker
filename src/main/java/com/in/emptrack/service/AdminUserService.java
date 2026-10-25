package com.in.emptrack.service;

import com.in.emptrack.dto.request.AdminProfileUpdateRequest;
import com.in.emptrack.dto.request.PasswordChangeRequest;
import com.in.emptrack.entity.AdminUser;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.exception.ResourceNotFoundException;
import com.in.emptrack.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AdminUser getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "admin";
        return adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + username));
    }

    @Transactional
    public void updateProfile(Long id, AdminProfileUpdateRequest request) {
        AdminUser admin = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        String email = request.getEmail().trim().toLowerCase();
        if (adminUserRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email '" + email + "' is already in use by another account.");
        }

        admin.setFullName(request.getFullName().trim());
        admin.setEmail(email);
        adminUserRepository.save(admin);
    }

    @Transactional
    public void changePassword(Long id, PasswordChangeRequest request) {
        AdminUser admin = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("The current password you entered is incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminUserRepository.save(admin);
    }
}
