package com.in.emptrack.service;

import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.entity.Department;
import com.in.emptrack.entity.Employee;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.exception.ResourceNotFoundException;
import com.in.emptrack.mapper.EmployeeMapper;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployees(String search, Long departmentId,
                                              EmploymentStatus status, EmploymentType employmentType,
                                              int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                resolveSortProperty(sortField));
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Specification<Employee> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String searchLike = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName"))), searchLike);
                Predicate codeLike = cb.like(cb.lower(root.get("employeeCode")), searchLike);
                Predicate emailLike = cb.like(cb.lower(root.get("email")), searchLike);
                Predicate designationLike = cb.like(cb.lower(root.get("designation")), searchLike);
                predicates.add(cb.or(nameLike, codeLike, emailLike, designationLike));
            }

            if (departmentId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), departmentId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (employmentType != null) {
                predicates.add(cb.equal(root.get("employmentType"), employmentType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        return employeePage.map(employeeMapper::toResponse);
    }

    private String resolveSortProperty(String field) {
        if (field == null) return "id";
        return switch (field) {
            case "name", "firstName" -> "firstName";
            case "lastName" -> "lastName";
            case "employeeCode", "code" -> "employeeCode";
            case "email" -> "email";
            case "department" -> "department.name";
            case "designation" -> "designation";
            case "salary" -> "salary";
            case "joiningDate" -> "joiningDate";
            case "status" -> "status";
            case "createdAt" -> "createdAt";
            default -> "id";
        };
    }

    @Transactional(readOnly = true)
    public Employee getEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getByEmployeeCode(String code) {
        return employeeRepository.findByEmployeeCodeIgnoreCase(code)
                .map(employeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + code));
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeFormRequest request) {
        String code = request.getEmployeeCode().trim().toUpperCase();
        String email = request.getEmail().trim().toLowerCase();

        if (employeeRepository.existsByEmployeeCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Employee Code '" + code + "' is already registered to another employee.");
        }
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email address '" + email + "' is already registered.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected department does not exist (ID: " + request.getDepartmentId() + ")"));

        Employee employee = employeeMapper.toEntity(request, department);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeFormRequest request) {
        Employee existing = getEntityById(id);
        String code = request.getEmployeeCode().trim().toUpperCase();
        String email = request.getEmail().trim().toLowerCase();

        if (employeeRepository.existsByEmployeeCodeIgnoreCaseAndIdNot(code, id)) {
            throw new DuplicateResourceException("Employee Code '" + code + "' is already registered to another employee.");
        }
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email address '" + email + "' is already in use by another employee.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected department does not exist (ID: " + request.getDepartmentId() + ")"));

        existing.setEmployeeCode(code);
        existing.setFirstName(request.getFirstName().trim());
        existing.setLastName(request.getLastName().trim());
        existing.setEmail(email);
        existing.setPhone(request.getPhone() != null && !request.getPhone().isBlank() ? request.getPhone().trim() : null);
        existing.setDepartment(department);
        existing.setDesignation(request.getDesignation().trim());
        existing.setEmploymentType(request.getEmploymentType());
        existing.setSalary(request.getSalary());
        existing.setLocation(request.getLocation() != null && !request.getLocation().isBlank() ? request.getLocation().trim() : null);
        existing.setJoiningDate(request.getJoiningDate());
        existing.setStatus(request.getStatus());

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toResponse(updated);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEntityById(id);
        employeeRepository.delete(employee);
    }

    @Transactional
    public EmployeeResponse toggleStatus(Long id) {
        Employee employee = getEntityById(id);
        if (employee.getStatus() == EmploymentStatus.ACTIVE) {
            employee.setStatus(EmploymentStatus.INACTIVE);
        } else {
            employee.setStatus(EmploymentStatus.ACTIVE);
        }
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public String generateNextEmployeeCode() {
        long count = employeeRepository.count();
        long candidateNumber = count + 1001;
        String candidate = "EMP-" + candidateNumber;
        while (employeeRepository.existsByEmployeeCodeIgnoreCase(candidate)) {
            candidateNumber++;
            candidate = "EMP-" + candidateNumber;
        }
        return candidate;
    }

    @Transactional(readOnly = true)
    public void writeEmployeesCsv(Writer writer) {
        PrintWriter pw = new PrintWriter(writer);

        pw.print('﻿');

        pw.println("Employee ID,First Name,Last Name,Email,Phone,Department,Designation,Employment Type,Salary,Location,Joining Date,Status");

        List<Employee> allEmployees = employeeRepository.findAllByOrderByLastNameAsc();
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

        for (Employee e : allEmployees) {
            pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    escapeCsv(e.getEmployeeCode()),
                    escapeCsv(e.getFirstName()),
                    escapeCsv(e.getLastName()),
                    escapeCsv(e.getEmail()),
                    escapeCsv(e.getPhone() != null ? e.getPhone() : ""),
                    escapeCsv(e.getDepartment() != null ? e.getDepartment().getName() : "Unassigned"),
                    escapeCsv(e.getDesignation()),
                    escapeCsv(e.getEmploymentType() != null ? e.getEmploymentType().getDisplayName() : ""),
                    escapeCsv(String.valueOf(e.getSalary() != null ? e.getSalary() : 0.0)),
                    escapeCsv(e.getLocation() != null ? e.getLocation() : ""),
                    escapeCsv(e.getJoiningDate() != null ? e.getJoiningDate().format(df) : ""),
                    escapeCsv(e.getStatus() != null ? e.getStatus().getDisplayName() : "")
            );
        }
        pw.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String sanitized = value.trim();
        if (!sanitized.isEmpty() && (sanitized.startsWith("=") || sanitized.startsWith("+")
                || sanitized.startsWith("-") || sanitized.startsWith("@") || sanitized.startsWith("\t") || sanitized.startsWith("\r"))) {
            sanitized = "'" + sanitized;
        }
        if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\n") || sanitized.contains("\r")) {
            sanitized = "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }
        return sanitized;
    }
}
