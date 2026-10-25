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
public class DashboardController {

    private final DashboardService dashboardService;
    private final AdminUserService adminUserService;

    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/dashboard";
    }
}
