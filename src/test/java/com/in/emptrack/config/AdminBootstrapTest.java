package com.in.emptrack.config;

import com.in.emptrack.entity.AdminUser;
import com.in.emptrack.repository.AdminUserRepository;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AdminBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new AdminBootstrap(adminUserRepository, departmentRepository, employeeRepository, passwordEncoder);
        ReflectionTestUtils.setField(bootstrap, "adminFullName", "Test Administrator");
        ReflectionTestUtils.setField(bootstrap, "seedDemoData", false);
    }

    @Test
    void createsAdminWithHashedPasswordWhenNoAdminExists() {
        when(adminUserRepository.existsByRole("ROLE_ADMIN")).thenReturn(false);
        ReflectionTestUtils.setField(bootstrap, "adminUsername", "firstadmin");
        ReflectionTestUtils.setField(bootstrap, "adminPassword", "SomeLongPassword-123");
        ReflectionTestUtils.setField(bootstrap, "adminEmail", "first@example.com");

        bootstrap.run();

        ArgumentCaptor<AdminUser> captor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).save(captor.capture());
        AdminUser saved = captor.getValue();
        assertEquals("firstadmin", saved.getUsername());
        assertNotEquals("SomeLongPassword-123", saved.getPassword());
        assertTrue(passwordEncoder.matches("SomeLongPassword-123", saved.getPassword()));
    }

    @Test
    void failsClearlyWhenCredentialsAreMissingAndNoAdminExists() {
        when(adminUserRepository.existsByRole("ROLE_ADMIN")).thenReturn(false);
        ReflectionTestUtils.setField(bootstrap, "adminUsername", "");
        ReflectionTestUtils.setField(bootstrap, "adminPassword", "");
        ReflectionTestUtils.setField(bootstrap, "adminEmail", "");

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> bootstrap.run());

        assertTrue(error.getMessage().contains("EMPTRACK_ADMIN_USERNAME"));
        verify(adminUserRepository, never()).save(any(AdminUser.class));
    }

    @Test
    void rejectsShortPassword() {
        when(adminUserRepository.existsByRole("ROLE_ADMIN")).thenReturn(false);
        ReflectionTestUtils.setField(bootstrap, "adminUsername", "firstadmin");
        ReflectionTestUtils.setField(bootstrap, "adminPassword", "short");
        ReflectionTestUtils.setField(bootstrap, "adminEmail", "");

        assertThrows(IllegalStateException.class, () -> bootstrap.run());
        verify(adminUserRepository, never()).save(any(AdminUser.class));
    }

    @Test
    void neverTouchesExistingAdminOrCredentials() {
        when(adminUserRepository.existsByRole("ROLE_ADMIN")).thenReturn(true);
        ReflectionTestUtils.setField(bootstrap, "adminUsername", "");
        ReflectionTestUtils.setField(bootstrap, "adminPassword", "");

        bootstrap.run();

        verify(adminUserRepository, never()).save(any(AdminUser.class));
    }

    @Test
    void createsDemoAccountsWithOwnRolesAndSkipsExistingOnes() {
        when(adminUserRepository.existsByRole("ROLE_ADMIN")).thenReturn(false);
        when(adminUserRepository.existsByUsername("demohr")).thenReturn(true);
        when(adminUserRepository.existsByUsername("demomanager")).thenReturn(false);
        when(adminUserRepository.findByEmailIgnoreCase("demomanager@emptrack.local")).thenReturn(java.util.Optional.empty());
        ReflectionTestUtils.setField(bootstrap, "adminUsername", "");
        ReflectionTestUtils.setField(bootstrap, "adminPassword", "");
        ReflectionTestUtils.setField(bootstrap, "adminEmail", "");
        ReflectionTestUtils.setField(bootstrap, "demoAccountsEnabled", true);

        bootstrap.run();

        ArgumentCaptor<AdminUser> captor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).save(captor.capture());
        AdminUser saved = captor.getValue();
        assertEquals("demomanager", saved.getUsername());
        assertEquals("ROLE_MANAGER", saved.getRole());
        assertTrue(passwordEncoder.matches("ManagerDemo@123", saved.getPassword()));
    }
}
