package com.example.demo.security.controller;

import com.example.demo.security.model.MyUser;
import com.example.demo.security.model.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContentController {

    @GetMapping("/home")
    public String handleWelcome() {
        return "/security/home";
    }

    @GetMapping("/admin/home")
    public String handleAdminHome() {
        return "/index";
    }

    @GetMapping("/user/home")
    public String handleUserHome() {
        return "/security/user_home";
    }

    @GetMapping("/login")
    public String handleLogin() {
        return "/security/login";
    }

    @GetMapping("/form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new MyUser());
        return "/security/register";
    }
}