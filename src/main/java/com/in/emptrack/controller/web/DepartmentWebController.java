package com.in.emptrack.controller.web;

import com.in.emptrack.dto.request.DepartmentFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.mapper.DepartmentMapper;
import com.in.emptrack.service.AdminUserService;
import com.in.emptrack.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentWebController {

    private final DepartmentService departmentService;
    private final AdminUserService adminUserService;
    private final DepartmentMapper departmentMapper;

    @GetMapping
    public String listDepartments(Model model) {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("activeNav", "departments");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/departments";
    }

    @GetMapping("/add")
    public String showAddDepartmentForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentFormRequest());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "departments");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/department-form";
    }

    @PostMapping("/add")
    public String createDepartment(
            @Valid @ModelAttribute("departmentForm") DepartmentFormRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeNav", "departments");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/department-form";
        }

        try {
            DepartmentResponse saved = departmentService.createDepartment(form);
            ra.addFlashAttribute("successMsg", "Department '" + saved.getName() + "' created successfully!");
            return "redirect:/departments";
        } catch (DuplicateResourceException ex) {
            bindingResult.reject("duplicate", ex.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeNav", "departments");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/department-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditDepartmentForm(@PathVariable Long id, Model model) {
        DepartmentResponse dept = departmentService.getById(id);
        DepartmentFormRequest form = departmentMapper.toFormRequest(departmentService.getEntityById(id));
        model.addAttribute("departmentForm", form);
        model.addAttribute("departmentId", id);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeNav", "departments");
        model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
        return "pages/department-form";
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("departmentForm") DepartmentFormRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departmentId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "departments");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/department-form";
        }

        try {
            DepartmentResponse updated = departmentService.updateDepartment(id, form);
            ra.addFlashAttribute("successMsg", "Department '" + updated.getName() + "' updated successfully!");
            return "redirect:/departments";
        } catch (DuplicateResourceException ex) {
            bindingResult.reject("duplicate", ex.getMessage());
            model.addAttribute("departmentId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "departments");
            model.addAttribute("currentAdmin", adminUserService.getCurrentAdmin());
            return "pages/department-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes ra) {
        DepartmentResponse dept = departmentService.getById(id);
        departmentService.deleteDepartment(id);
        ra.addFlashAttribute("successMsg", "Department '" + dept.getName() + "' was deleted.");
        return "redirect:/departments";
    }
}
