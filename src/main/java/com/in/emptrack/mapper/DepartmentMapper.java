package com.in.emptrack.mapper;

import com.in.emptrack.dto.request.DepartmentFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentFormRequest req) {
        if (req == null) return null;
        return Department.builder()
                .id(req.getId())
                .departmentCode(req.getDepartmentCode().trim().toUpperCase())
                .name(req.getName().trim())
                .description(req.getDescription() != null ? req.getDescription().trim() : null)
                .location(req.getLocation() != null ? req.getLocation().trim() : null)
                .build();
    }

    public DepartmentResponse toResponse(Department dept) {
        if (dept == null) return null;
        return DepartmentResponse.builder()
                .id(dept.getId())
                .departmentCode(dept.getDepartmentCode())
                .name(dept.getName())
                .description(dept.getDescription())
                .location(dept.getLocation())
                .employeeCount(dept.getEmployees() != null ? dept.getEmployees().size() : 0)
                .createdAt(dept.getCreatedAt())
                .updatedAt(dept.getUpdatedAt())
                .build();
    }

    public DepartmentFormRequest toFormRequest(Department dept) {
        if (dept == null) return null;
        return DepartmentFormRequest.builder()
                .id(dept.getId())
                .departmentCode(dept.getDepartmentCode())
                .name(dept.getName())
                .description(dept.getDescription())
                .location(dept.getLocation())
                .build();
    }
}
