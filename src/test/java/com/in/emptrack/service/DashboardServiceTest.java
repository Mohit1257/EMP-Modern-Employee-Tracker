package com.in.emptrack.service;

import com.in.emptrack.dto.response.DashboardSummaryResponse;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.mapper.EmployeeMapper;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapper();

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should aggregate dashboard summary counts and payroll accurately")
    void testGetDashboardSummary() {
        when(employeeRepository.count()).thenReturn(12L);
        when(employeeRepository.countByStatus(EmploymentStatus.ACTIVE)).thenReturn(10L);
        when(employeeRepository.countByStatus(EmploymentStatus.INACTIVE)).thenReturn(1L);
        when(employeeRepository.countByStatus(EmploymentStatus.ON_LEAVE)).thenReturn(1L);
        when(departmentRepository.count()).thenReturn(6L);
        when(employeeRepository.sumSalaryByStatus(EmploymentStatus.ACTIVE)).thenReturn(840000.0);

        List<Object[]> deptCounts = new ArrayList<>();
        deptCounts.add(new Object[]{"Engineering", 4L});
        deptCounts.add(new Object[]{"HR", 2L});
        when(departmentRepository.countEmployeesByDepartment()).thenReturn(deptCounts);

        when(employeeRepository.countByEmploymentTypeGroup()).thenReturn(Collections.emptyList());
        when(employeeRepository.findTop5ByOrderByIdDesc()).thenReturn(Collections.emptyList());
        when(employeeRepository.countHiresByMonth()).thenReturn(Collections.emptyList());

        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();

        assertNotNull(summary);
        assertEquals(12, summary.getTotalEmployees());
        assertEquals(10, summary.getActiveEmployees());
        assertEquals(1, summary.getInactiveEmployees());
        assertEquals(6, summary.getTotalDepartments());
        assertEquals(840000.0, summary.getTotalSalaryExpense());
        assertEquals(2, summary.getDepartmentDistribution().size());
        assertEquals(4L, summary.getDepartmentDistribution().get("Engineering"));
    }
}
