package com.in.emptrack.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    private Long id;
    private String departmentCode;
    private String name;
    private String description;
    private String location;
    private int employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
