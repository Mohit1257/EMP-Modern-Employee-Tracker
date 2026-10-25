package com.in.emptrack.mapper;

import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.entity.Department;
import com.in.emptrack.entity.Employee;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class EmployeeMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    public Employee toEntity(EmployeeFormRequest req, Department department) {
        if (req == null) return null;
        return Employee.builder()
                .id(req.getId())
                .employeeCode(req.getEmployeeCode().trim().toUpperCase())
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(req.getEmail().trim().toLowerCase())
                .phone(req.getPhone() != null && !req.getPhone().isBlank() ? req.getPhone().trim() : null)
                .department(department)
                .designation(req.getDesignation().trim())
                .employmentType(req.getEmploymentType())
                .salary(req.getSalary())
                .location(req.getLocation() != null && !req.getLocation().isBlank() ? req.getLocation().trim() : null)
                .joiningDate(req.getJoiningDate())
                .status(req.getStatus())
                .build();
    }

    public EmployeeResponse toResponse(Employee emp) {
        if (emp == null) return null;
        String formattedSalary = emp.getSalary() != null ? CURRENCY_FORMATTER.format(emp.getSalary()) : "$0.00";
        String formattedJoining = emp.getJoiningDate() != null ? emp.getJoiningDate().format(DATE_FORMATTER) : "—";

        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .fullName(emp.getFullName())
                .initials(emp.getInitials())
                .email(emp.getEmail())
                .phone(emp.getPhone() != null ? emp.getPhone() : "—")
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : "Unassigned")
                .departmentCode(emp.getDepartment() != null ? emp.getDepartment().getDepartmentCode() : "N/A")
                .designation(emp.getDesignation())
                .employmentType(emp.getEmploymentType())
                .employmentTypeDisplay(emp.getEmploymentType() != null ? emp.getEmploymentType().getDisplayName() : "")
                .salary(emp.getSalary())
                .formattedSalary(formattedSalary)
                .location(emp.getLocation() != null && !emp.getLocation().isBlank() ? emp.getLocation() : "HQ / Remote")
                .joiningDate(emp.getJoiningDate())
                .formattedJoiningDate(formattedJoining)
                .status(emp.getStatus())
                .statusDisplay(emp.getStatus() != null ? emp.getStatus().getDisplayName() : "")
                .statusBadgeClass(emp.getStatus() != null ? emp.getStatus().getBadgeClass() : "secondary")
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt())
                .build();
    }

    public EmployeeFormRequest toFormRequest(Employee emp) {
        if (emp == null) return null;
        return EmployeeFormRequest.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .designation(emp.getDesignation())
                .employmentType(emp.getEmploymentType())
                .salary(emp.getSalary())
                .location(emp.getLocation())
                .joiningDate(emp.getJoiningDate())
                .status(emp.getStatus())
                .build();
    }
}
