package com.in.emptrack.dto.request;

import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeFormRequest {

    private Long id;

    @NotBlank(message = "Employee ID/Code is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]{3,20}$", message = "Employee Code must be 3-20 characters (alphanumeric, dash, underscore)")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 60, message = "First name must be between 2 and 60 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 60, message = "Last name must be between 1 and 60 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid corporate email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Pattern(regexp = "^$|^[+0-9\s-()]{7,25}$", message = "Please enter a valid phone number format")
    private String phone;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotBlank(message = "Designation/Job Title is required")
    @Size(min = 2, max = 100, message = "Designation must be between 2 and 100 characters")
    private String designation;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Salary must be greater than zero")
    private Double salary;

    @Size(max = 100, message = "Office location cannot exceed 100 characters")
    private String location;

    @NotNull(message = "Joining date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joiningDate;

    @NotNull(message = "Employment status is required")
    private EmploymentStatus status;
}
