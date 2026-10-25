package com.in.emptrack.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Authentication authentication,
            Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password. Please verify your credentials.");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been securely signed out.");
        }

        return "pages/login";
    }
}
