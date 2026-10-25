package com.in.emptrack.controller.web;

import com.in.emptrack.dto.response.DashboardSummaryResponse;
import com.in.emptrack.service.AdminUserService;
import com.in.emptrack.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReportsWebController {

    private final DashboardService dashboardService;
    private final AdminUserService adminUserService;

    @GetMapping("/reports")
    public String reports(Model model) {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("activeNav", "reports");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/reports";
    }
}
