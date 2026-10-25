package com.in.emptrack.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long onLeaveEmployees;
    private long totalDepartments;
    private double totalSalaryExpense;
    private String formattedSalaryExpense;
    private Map<String, Long> departmentDistribution;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> employmentTypeDistribution;
    private List<MonthlyTrendDto> monthlyTrends;
    private List<EmployeeResponse> recentHires;
}
