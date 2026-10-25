package com.in.emptrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentFormRequest {

    private Long id;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 10, message = "Code must be 2-10 characters (e.g. ENG, HR)")
    @Pattern(regexp = "^[A-Za-z0-9]{2,10}$", message = "Code must be alphanumeric")
    private String departmentCode;

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;
}
