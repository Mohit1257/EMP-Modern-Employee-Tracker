package com.in.emptrack.dto.response;

import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String initials;
    private String email;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String designation;
    private EmploymentType employmentType;
    private String employmentTypeDisplay;
    private Double salary;
    private String formattedSalary;
    private String location;
    private LocalDate joiningDate;
    private String formattedJoiningDate;
    private EmploymentStatus status;
    private String statusDisplay;
    private String statusBadgeClass;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
