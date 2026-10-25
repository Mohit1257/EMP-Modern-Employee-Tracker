package com.in.emptrack.service;

import com.in.emptrack.dto.request.DepartmentFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.entity.Department;
import com.in.emptrack.exception.DepartmentNotEmptyException;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.exception.ResourceNotFoundException;
import com.in.emptrack.mapper.DepartmentMapper;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAllByOrderByNameAsc()
                .stream()
                .map(departmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Department getEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return departmentMapper.toResponse(getEntityById(id));
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentFormRequest request) {
        String code = request.getDepartmentCode().trim().toUpperCase();
        String name = request.getName().trim();

        if (departmentRepository.existsByDepartmentCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Department code '" + code + "' is already in use.");
        }
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Department name '" + name + "' already exists.");
        }

        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponse(saved);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentFormRequest request) {
        Department existing = getEntityById(id);
        String code = request.getDepartmentCode().trim().toUpperCase();
        String name = request.getName().trim();

        if (departmentRepository.existsByDepartmentCodeIgnoreCaseAndIdNot(code, id)) {
            throw new DuplicateResourceException("Department code '" + code + "' is already in use by another department.");
        }
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Department name '" + name + "' already exists.");
        }

        existing.setDepartmentCode(code);
        existing.setName(name);
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        existing.setLocation(request.getLocation() != null ? request.getLocation().trim() : null);

        Department updated = departmentRepository.save(existing);
        return departmentMapper.toResponse(updated);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getEntityById(id);
        long count = employeeRepository.countByDepartmentId(id);
        if (count > 0) {
            throw new DepartmentNotEmptyException(
                String.format("Cannot delete department '%s' because %d employee(s) are currently assigned to it. Please reassign the employees before deleting.",
                        department.getName(), count)
            );
        }
        departmentRepository.delete(department);
    }
}
