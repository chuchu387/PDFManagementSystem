package com.example.pdfmanagement.controller;

import com.example.pdfmanagement.model.AppUser;
import com.example.pdfmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/create-user")
    public String createUserForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "create-user";
    }

    @PostMapping("/create-user")
    public String createUser(AppUser user) {
        userService.registerUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @PostMapping("/block-user")
    public String blockUser(@RequestParam("userId") Long userId) {
        userService.deactivateUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/activate-user")
    public String activateUser(@RequestParam("userId") Long userId) {
        userService.activateUser(userId);
        return "redirect:/admin/users";
    }
}
