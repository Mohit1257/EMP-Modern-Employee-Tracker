package com.in.emptrack.controller.web;

import com.in.emptrack.dto.request.EmployeeFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.dto.response.EmployeeResponse;
import com.in.emptrack.enums.EmploymentStatus;
import com.in.emptrack.enums.EmploymentType;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.mapper.EmployeeMapper;
import com.in.emptrack.service.AdminUserService;
import com.in.emptrack.service.DepartmentService;
import com.in.emptrack.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final AdminUserService adminUserService;
    private final EmployeeMapper employeeMapper;

    @GetMapping
    public String listEmployees(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            Model model) {

        int validSize = (size == 8 || size == 15 || size == 25 || size == 50) ? size : 8;
        Page<EmployeeResponse> employeePage = employeeService.getEmployees(
                search, departmentId, status, employmentType, page, validSize, sort, dir);

        List<DepartmentResponse> departments = departmentService.getAllDepartments();

        model.addAttribute("employeePage", employeePage);
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("departments", departments);
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("employmentTypes", EmploymentType.values());

        model.addAttribute("search", search);
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", employmentType);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", validSize);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);
        model.addAttribute("reverseDir", "asc".equalsIgnoreCase(dir) ? "desc" : "asc");

        model.addAttribute("activeNav", "employees");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/employees";
    }

    @GetMapping("/view/{id}")
    public String viewEmployee(@PathVariable Long id, Model model) {
        EmployeeResponse employee = employeeService.getById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("activeNav", "employees");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/employee-detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        EmployeeFormRequest form = new EmployeeFormRequest();
        form.setEmployeeCode(employeeService.generateNextEmployeeCode());
        form.setStatus(EmploymentStatus.ACTIVE);
        form.setEmploymentType(EmploymentType.FULL_TIME);

        model.addAttribute("employeeForm", form);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "employees");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/employee-form";
    }

    @PostMapping("/add")
    public String handleAddEmployee(
            @Valid @ModelAttribute("employeeForm") EmployeeFormRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("employmentTypes", EmploymentType.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeNav", "employees");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/employee-form";
        }

        try {
            EmployeeResponse saved = employeeService.createEmployee(form);
            ra.addFlashAttribute("successMsg", "Employee " + saved.getFullName() + " (" + saved.getEmployeeCode() + ") added successfully!");
            return "redirect:/employees";
        } catch (DuplicateResourceException ex) {
            bindingResult.reject("duplicate", ex.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("employmentTypes", EmploymentType.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeNav", "employees");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/employee-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeeResponse employee = employeeService.getById(id);
        EmployeeFormRequest form = employeeMapper.toFormRequest(employeeService.getEntityById(id));

        model.addAttribute("employeeForm", form);
        model.addAttribute("employeeId", id);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeNav", "employees");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/employee-form";
    }

    @PostMapping("/edit/{id}")
    public String handleEditEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employeeForm") EmployeeFormRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("employmentTypes", EmploymentType.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "employees");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/employee-form";
        }

        try {
            EmployeeResponse updated = employeeService.updateEmployee(id, form);
            ra.addFlashAttribute("successMsg", "Employee " + updated.getFullName() + " updated successfully!");
            return "redirect:/employees";
        } catch (DuplicateResourceException ex) {
            bindingResult.reject("duplicate", ex.getMessage());
            model.addAttribute("employeeId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("employmentTypes", EmploymentType.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "employees");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/employee-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {
        EmployeeResponse emp = employeeService.getById(id);
        employeeService.deleteEmployee(id);
        ra.addFlashAttribute("successMsg", "Employee record for " + emp.getFullName() + " was deleted.");
        return "redirect:/employees";
    }

    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes ra) {
        EmployeeResponse toggled = employeeService.toggleStatus(id);
        ra.addFlashAttribute("successMsg", "Status of " + toggled.getFullName() + " changed to " + toggled.getStatusDisplay() + ".");
        return "redirect:/employees";
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"employees_export.csv\"");
        employeeService.writeEmployeesCsv(response.getWriter());
    }
}
