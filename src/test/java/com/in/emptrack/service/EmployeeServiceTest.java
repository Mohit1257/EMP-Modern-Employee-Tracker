package com.in.emptrack.service;

import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.entity.Department;
import com.in.emptrack.entity.Employee;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.mapper.EmployeeMapper;
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

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapper();

    @InjectMocks
    private EmployeeService employeeService;

    private Department dept;
    private Employee emp;

    @BeforeEach
    void setUp() {
        dept = Department.builder()
                .id(1L)
                .departmentCode("ENG")
                .name("Engineering")
                .build();

        emp = Employee.builder()
                .id(10L)
                .employeeCode("EMP-1001")
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .phone("+91 98201 11223")
                .department(dept)
                .designation("Principal Engineer")
                .employmentType(EmploymentType.FULL_TIME)
                .salary(95000.0)
                .location("Pune")
                .joiningDate(LocalDate.of(2023, 1, 15))
                .status(EmploymentStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create employee when code and email are unique")
    void testCreateEmployeeSuccess() {
        EmployeeFormRequest req = EmployeeFormRequest.builder()
                .employeeCode("EMP-1001")
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .departmentId(1L)
                .designation("Principal Engineer")
                .employmentType(EmploymentType.FULL_TIME)
                .salary(95000.0)
                .location("Pune")
                .joiningDate(LocalDate.of(2023, 1, 15))
                .status(EmploymentStatus.ACTIVE)
                .build();

        when(employeeRepository.existsByEmployeeCodeIgnoreCase("EMP-1001")).thenReturn(false);
        when(employeeRepository.existsByEmailIgnoreCase("aarav.sharma@example.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(employeeRepository.save(any(Employee.class))).thenReturn(emp);

        EmployeeResponse resp = employeeService.createEmployee(req);

        assertNotNull(resp);
        assertEquals("EMP-1001", resp.getEmployeeCode());
        assertEquals("Aarav Sharma", resp.getFullName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on duplicate employee code")
    void testCreateEmployeeDuplicateCode() {
        EmployeeFormRequest req = EmployeeFormRequest.builder()
                .employeeCode("EMP-1001")
                .email("test@example.com")
                .build();

        when(employeeRepository.existsByEmployeeCodeIgnoreCase("EMP-1001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(req));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should toggle status from ACTIVE to INACTIVE")
    void testToggleStatus() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(emp));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        EmployeeResponse toggled = employeeService.toggleStatus(10L);

        assertEquals(EmploymentStatus.INACTIVE, toggled.getStatus());
        assertEquals("Inactive", toggled.getStatusDisplay());
    }

    @Test
    @DisplayName("Should write CSV properly and mitigate formula injection")
    void testWriteEmployeesCsv() {
        Employee formulaEmp = Employee.builder()
                .id(20L)
                .employeeCode("=cmd|'/C calc'!A0")
                .firstName("@Admin")
                .lastName("+Smith")
                .email("test.injection@example.com")
                .department(dept)
                .designation("-Director")
                .employmentType(EmploymentType.FULL_TIME)
                .salary(100000.0)
                .location("Pune")
                .joiningDate(LocalDate.of(2024, 1, 1))
                .status(EmploymentStatus.ACTIVE)
                .build();

        when(employeeRepository.findAllByOrderByLastNameAsc()).thenReturn(List.of(formulaEmp));

        StringWriter sw = new StringWriter();
        employeeService.writeEmployeesCsv(sw);

        String output = sw.toString();

        assertTrue(output.startsWith("﻿"));

        assertTrue(output.contains("''=cmd|'/C calc'!A0") || output.contains("'=cmd"));
        assertTrue(output.contains("'+Smith"));
        assertTrue(output.contains("'-Director"));
    }
}
