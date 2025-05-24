package com.ensah.nlp_annotation.controller;


import com.ensah.nlp_annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String index(Model model) {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboardAdmin() {
        return "admin/dashboard"; // → fichier dashboard.html dans templates
    }

}
