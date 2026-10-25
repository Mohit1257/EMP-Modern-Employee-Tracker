package com.in.emptrack.service;

import com.in.emptrack.dto.response.DashboardSummaryResponse;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.dto.response.MonthlyTrendDto;
import com.in.emptrack.entity.Employee;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.mapper.EmployeeMapper;
import com.in.emptrack.repository.DepartmentRepository;
import com.in.emptrack.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus(EmploymentStatus.ACTIVE);
        long inactiveEmployees = employeeRepository.countByStatus(EmploymentStatus.INACTIVE);
        long onLeaveEmployees = employeeRepository.countByStatus(EmploymentStatus.ON_LEAVE);
        long totalDepartments = departmentRepository.count();

        Double totalSalaryExpense = employeeRepository.sumSalaryByStatus(EmploymentStatus.ACTIVE);
        if (totalSalaryExpense == null) totalSalaryExpense = 0.0;
        String formattedSalary = CURRENCY_FORMAT.format(totalSalaryExpense);

        Map<String, Long> deptDistribution = new LinkedHashMap<>();
        List<Object[]> rawDeptCounts = departmentRepository.countEmployeesByDepartment();
        for (Object[] row : rawDeptCounts) {
            String deptName = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            deptDistribution.put(deptName, count);
        }

        Map<String, Long> statusDistribution = new LinkedHashMap<>();
        statusDistribution.put("Active", activeEmployees);
        statusDistribution.put("Inactive", inactiveEmployees);
        statusDistribution.put("On Leave", onLeaveEmployees);

        Map<String, Long> typeDistribution = new LinkedHashMap<>();
        for (EmploymentType type : EmploymentType.values()) {
            typeDistribution.put(type.getDisplayName(), 0L);
        }
        List<Object[]> rawTypeCounts = employeeRepository.countByEmploymentTypeGroup();
        for (Object[] row : rawTypeCounts) {
            EmploymentType type = (EmploymentType) row[0];
            if (type != null) {
                Long count = ((Number) row[1]).longValue();
                typeDistribution.put(type.getDisplayName(), count);
            }
        }

        List<Employee> recentList = employeeRepository.findTop5ByOrderByIdDesc();
        List<EmployeeResponse> recentHires = recentList.stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());

        List<MonthlyTrendDto> monthlyTrends = new ArrayList<>();
        List<Object[]> rawTrends = employeeRepository.countHiresByMonth();
        String[] months = new DateFormatSymbols().getShortMonths();

        for (Object[] row : rawTrends) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            String monthName = (month >= 1 && month <= 12) ? months[month - 1] : String.valueOf(month);
            monthlyTrends.add(new MonthlyTrendDto(monthName + " " + year, count));
        }

        return DashboardSummaryResponse.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .inactiveEmployees(inactiveEmployees)
                .onLeaveEmployees(onLeaveEmployees)
                .totalDepartments(totalDepartments)
                .totalSalaryExpense(totalSalaryExpense)
                .formattedSalaryExpense(formattedSalary)
                .departmentDistribution(deptDistribution)
                .statusDistribution(statusDistribution)
                .employmentTypeDistribution(typeDistribution)
                .monthlyTrends(monthlyTrends)
                .recentHires(recentHires)
                .build();
    }
}
