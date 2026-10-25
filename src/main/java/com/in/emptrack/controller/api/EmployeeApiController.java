package com.in.emptrack.controller.api;

import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.ApiResponse;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {

        Page<EmployeeResponse> result = employeeService.getEmployees(
                search, departmentId, status, employmentType, page, size, sort, dir);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee found", employee));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeFormRequest request) {
        EmployeeResponse created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeFormRequest request) {
        EmployeeResponse updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<EmployeeResponse>> toggleStatus(@PathVariable Long id) {
        EmployeeResponse toggled = employeeService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Status updated", toggled));
    }
}
