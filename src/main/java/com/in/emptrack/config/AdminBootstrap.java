package com.in.emptrack.config;

import com.in.emptrack.entity.AdminUser;
import com.in.emptrack.entity.Department;
import com.in.emptrack.entity.Employee;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.repository.AdminUserRepository;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Value("${emptrack.admin.username:}")
    private String adminUsername;

    @Value("${emptrack.admin.password:}")
    private String adminPassword;

    @Value("${emptrack.admin.email:}")
    private String adminEmail;

    @Value("${emptrack.admin.full-name:System Administrator}")
    private String adminFullName;

    @Value("${emptrack.seed-demo-data:true}")
    private boolean seedDemoData;

    @Value("${emptrack.demo-accounts.enabled:true}")
    private boolean demoAccountsEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        bootstrapAdminUser();
        if (demoAccountsEnabled) {
            bootstrapDemoAccounts();
        }
        if (seedDemoData) {
            bootstrapInitialDataIfEmpty();
        }
    }

    private void bootstrapAdminUser() {
        if (adminUserRepository.existsByRole(ROLE_ADMIN)) {
            log.info("Admin account already exists. Skipping Admin bootstrap.");
            return;
        }

        if (isBlank(adminUsername) || isBlank(adminPassword)) {
            if (demoAccountsEnabled) {
                log.warn("No Admin account was created because EMPTRACK_ADMIN_USERNAME and EMPTRACK_ADMIN_PASSWORD are not set. The demo accounts can be used to sign in.");
                return;
            }
            throw new IllegalStateException(
                    "No Admin account exists and the first-run Admin credentials are missing. "
                            + "Set the environment variables EMPTRACK_ADMIN_USERNAME and EMPTRACK_ADMIN_PASSWORD "
                            + "(optionally EMPTRACK_ADMIN_EMAIL and EMPTRACK_ADMIN_NAME) and start the application again.");
        }

        if (adminPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "EMPTRACK_ADMIN_PASSWORD must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }

        String username = adminUsername.trim();
        String email = isBlank(adminEmail) ? username + "@emptrack.local" : adminEmail.trim().toLowerCase();

        AdminUser admin = AdminUser.builder()
                .username(username)
                .password(passwordEncoder.encode(adminPassword))
                .fullName(isBlank(adminFullName) ? "System Administrator" : adminFullName.trim())
                .email(email)
                .role(ROLE_ADMIN)
                .build();
        adminUserRepository.save(admin);
        log.info("Initial Admin account '{}' was created.", username);
    }

    private void bootstrapDemoAccounts() {
        createDemoAccountIfMissing("demohr", "HrDemo@123", "Demo HR", "demohr@emptrack.local", "ROLE_HR");
        createDemoAccountIfMissing("demomanager", "ManagerDemo@123", "Demo Manager", "demomanager@emptrack.local", "ROLE_MANAGER");
    }

    private void createDemoAccountIfMissing(String username, String password, String fullName, String email, String role) {
        if (adminUserRepository.existsByUsername(username) || adminUserRepository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }
        adminUserRepository.save(AdminUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .email(email)
                .role(role)
                .build());
        log.info("Demo account '{}' was created with role {}.", username, role);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void bootstrapInitialDataIfEmpty() {
        if (departmentRepository.count() == 0) {
            log.info("No departments found. Initializing corporate departments...");

            Department eng = departmentRepository.save(Department.builder()
                    .departmentCode("ENG")
                    .name("Engineering")
                    .description("Software engineering, systems architecture, and product development")
                    .location("Tech Center, Floor 4")
                    .build());

            Department hr = departmentRepository.save(Department.builder()
                    .departmentCode("HR")
                    .name("Human Resources")
                    .description("Talent acquisition, culture, employee experience, and HR operations")
                    .location("Corporate HQ, Floor 2")
                    .build());

            Department mkt = departmentRepository.save(Department.builder()
                    .departmentCode("MKT")
                    .name("Marketing")
                    .description("Brand management, digital marketing, PR, and growth")
                    .location("Creative Hub, Floor 3")
                    .build());

            Department fin = departmentRepository.save(Department.builder()
                    .departmentCode("FIN")
                    .name("Finance")
                    .description("Corporate accounting, financial planning, auditing, and payroll")
                    .location("Financial Tower, Floor 5")
                    .build());

            Department sal = departmentRepository.save(Department.builder()
                    .departmentCode("SAL")
                    .name("Sales")
                    .description("Enterprise client partnerships, accounts, and business development")
                    .location("Sales Pavilion, Floor 2")
                    .build());

            Department ops = departmentRepository.save(Department.builder()
                    .departmentCode("OPS")
                    .name("Operations")
                    .description("Workforce logistics, facilities, infrastructure, and operational excellence")
                    .location("Main Building, Floor 1")
                    .build());

            log.info("Initialized 6 core departments.");

            if (employeeRepository.count() == 0) {
                log.info("No employees found. Seeding initial workforce records from existing project dataset...");

                seedEmployee("EMP-1001", "Aarav", "Sharma", "aarav.sharma@example.com", "+91 98201 11223", eng,
                        "Principal Software Engineer", EmploymentType.FULL_TIME, 95000.0, "Pune", LocalDate.of(2023, 1, 15), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1002", "Priya", "Patel", "priya.patel@example.com", "+91 98202 22334", hr,
                        "HR Operations Manager", EmploymentType.FULL_TIME, 68000.0, "Mumbai", LocalDate.of(2022, 6, 10), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1003", "Rohit", "Verma", "rohit.verma@example.com", "+91 98203 33445", eng,
                        "Senior Backend Engineer", EmploymentType.FULL_TIME, 88000.0, "Bengaluru", LocalDate.of(2021, 9, 1), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1004", "Sneha", "Joshi", "sneha.joshi@example.com", "+91 98204 44556", mkt,
                        "Digital Marketing Lead", EmploymentType.FULL_TIME, 62000.0, "Pune", LocalDate.of(2023, 3, 22), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1005", "Vikram", "Singh", "vikram.singh@example.com", "+91 98205 55667", fin,
                        "Financial Analyst", EmploymentType.CONTRACT, 70000.0, "Delhi", LocalDate.of(2020, 11, 5), EmploymentStatus.INACTIVE);

                seedEmployee("EMP-1006", "Ananya", "Gupta", "ananya.gupta@example.com", "+91 98206 66778", eng,
                        "Full Stack Developer", EmploymentType.FULL_TIME, 75000.0, "Hyderabad", LocalDate.of(2022, 8, 17), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1007", "Karan", "Malhotra", "karan.malhotra@example.com", "+91 98207 77889", sal,
                        "Enterprise Account Executive", EmploymentType.FULL_TIME, 56000.0, "Chennai", LocalDate.of(2023, 7, 1), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1008", "Deepika", "Nair", "deepika.nair@example.com", "+91 98208 88990", hr,
                        "Senior Talent Partner", EmploymentType.FULL_TIME, 60000.0, "Kochi", LocalDate.of(2021, 4, 20), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1009", "Arjun", "Reddy", "arjun.reddy@example.com", "+91 98209 99001", eng,
                        "DevOps & Cloud Architect", EmploymentType.FULL_TIME, 98000.0, "Bengaluru", LocalDate.of(2019, 12, 1), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1010", "Meera", "Iyer", "meera.iyer@example.com", "+91 98210 10112", fin,
                        "Senior Controller", EmploymentType.FULL_TIME, 78000.0, "Mumbai", LocalDate.of(2022, 2, 14), EmploymentStatus.ON_LEAVE);

                seedEmployee("EMP-1011", "Nikhil", "Desai", "nikhil.desai@example.com", "+91 98211 11223", mkt,
                        "Content & Growth Specialist", EmploymentType.FULL_TIME, 59000.0, "Ahmedabad", LocalDate.of(2023, 5, 30), EmploymentStatus.ACTIVE);

                seedEmployee("EMP-1012", "Pooja", "Kulkarni", "pooja.kulkarni@example.com", "+91 98212 12334", sal,
                        "Business Development Representative", EmploymentType.INTERN, 42000.0, "Pune", LocalDate.of(2022, 10, 11), EmploymentStatus.ACTIVE);

                log.info("Initialized 12 employee records with verified historical dates and departments.");
            }
        }
    }

    private void seedEmployee(String code, String firstName, String lastName, String email, String phone,
                              Department dept, String designation, EmploymentType type, Double salary,
                              String location, LocalDate joining, EmploymentStatus status) {
        employeeRepository.save(Employee.builder()
                .employeeCode(code)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .department(dept)
                .designation(designation)
                .employmentType(type)
                .salary(salary)
                .location(location)
                .joiningDate(joining)
                .status(status)
                .build());
    }
}
