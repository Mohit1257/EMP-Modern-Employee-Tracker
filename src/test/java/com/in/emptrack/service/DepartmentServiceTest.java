package com.in.emptrack.service;

import com.in.emptrack.dto.request.DepartmentFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.entity.Department;
import com.in.emptrack.exception.DepartmentNotEmptyException;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.mapper.DepartmentMapper;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private DepartmentMapper departmentMapper = new DepartmentMapper();

    @InjectMocks
    private DepartmentService departmentService;

    private Department dept;

    @BeforeEach
    void setUp() {
        dept = Department.builder()
                .id(1L)
                .departmentCode("ENG")
                .name("Engineering")
                .description("Tech division")
                .location("Floor 4")
                .build();
    }

    @Test
    @DisplayName("Should create department successfully when code and name are unique")
    void testCreateDepartmentSuccess() {
        DepartmentFormRequest req = DepartmentFormRequest.builder()
                .departmentCode("ENG")
                .name("Engineering")
                .description("Tech division")
                .location("Floor 4")
                .build();

        when(departmentRepository.existsByDepartmentCodeIgnoreCase("ENG")).thenReturn(false);
        when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(dept);

        DepartmentResponse resp = departmentService.createDepartment(req);

        assertNotNull(resp);
        assertEquals("ENG", resp.getDepartmentCode());
        assertEquals("Engineering", resp.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when department code exists")
    void testCreateDepartmentDuplicateCode() {
        DepartmentFormRequest req = DepartmentFormRequest.builder()
                .departmentCode("ENG")
                .name("Engineering")
                .build();

        when(departmentRepository.existsByDepartmentCodeIgnoreCase("ENG")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departmentService.createDepartment(req));
        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DepartmentNotEmptyException when trying to delete department with employees")
    void testDeleteDepartmentWithAssignedEmployees() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(5L);

        assertThrows(DepartmentNotEmptyException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should delete department successfully when no employees are assigned")
    void testDeleteDepartmentEmptySuccess() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(0L);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, times(1)).delete(dept);
    }
}
