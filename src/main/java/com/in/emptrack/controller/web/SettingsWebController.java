package com.in.emptrack.controller.web;

import com.in.emptrack.dto.request.AdminProfileUpdateRequest;
import com.in.emptrack.dto.request.PasswordChangeRequest;
import com.in.emptrack.entity.AdminUser;
import com.in.emptrack.exception.DuplicateResourceException;
import com.in.emptrack.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsWebController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String settings(Model model) {
        AdminUser admin = adminUserService.getCurrentAdmin();
        AdminProfileUpdateRequest profileForm = AdminProfileUpdateRequest.builder()
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .build();

        model.addAttribute("admin", admin);
        model.addAttribute("profileForm", profileForm);
        model.addAttribute("passwordForm", new PasswordChangeRequest());
        model.addAttribute("activeNav", "settings");
        model.addAttribute("currentAdmin", admin);
        return "pages/settings";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") AdminProfileUpdateRequest profileForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        AdminUser admin = adminUserService.getCurrentAdmin();
        if (bindingResult.hasErrors()) {
            model.addAttribute("admin", admin);
            model.addAttribute("passwordForm", new PasswordChangeRequest());
            model.addAttribute("activeNav", "settings");
            model.addAttribute("currentAdmin", admin);
            return "pages/settings";
        }

        try {
            adminUserService.updateProfile(admin.getId(), profileForm);
            ra.addFlashAttribute("successMsg", "Admin profile details successfully updated.");
            return "redirect:/settings";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("admin", admin);
            model.addAttribute("passwordForm", new PasswordChangeRequest());
            model.addAttribute("activeNav", "settings");
            model.addAttribute("currentAdmin", admin);
            return "pages/settings";
        }
    }

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordForm") PasswordChangeRequest passwordForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        AdminUser admin = adminUserService.getCurrentAdmin();
        if (bindingResult.hasErrors()) {
            AdminProfileUpdateRequest profileForm = AdminProfileUpdateRequest.builder()
                    .fullName(admin.getFullName())
                    .email(admin.getEmail())
                    .build();
            model.addAttribute("admin", admin);
            model.addAttribute("profileForm", profileForm);
            model.addAttribute("activeNav", "settings");
            model.addAttribute("currentAdmin", admin);
            return "pages/settings";
        }

        try {
            adminUserService.changePassword(admin.getId(), passwordForm);
            ra.addFlashAttribute("successMsg", "Your password has been changed successfully.");
            return "redirect:/settings";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("passwordError", ex.getMessage());
            AdminProfileUpdateRequest profileForm = AdminProfileUpdateRequest.builder()
                    .fullName(admin.getFullName())
                    .email(admin.getEmail())
                    .build();
            model.addAttribute("admin", admin);
            model.addAttribute("profileForm", profileForm);
            model.addAttribute("activeNav", "settings");
            model.addAttribute("currentAdmin", admin);
            return "pages/settings";
        }
    }
}
