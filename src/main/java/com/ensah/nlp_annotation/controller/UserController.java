package com.ensah.nlp_annotation.controller;

import com.ensah.nlp_annotation.entity.AppUser;
import com.ensah.nlp_annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/dashboard")
    public String dashboardUser() {
        System.out.println("\n\n\nAccès au tableau de bord de l'utilisateur\n\n\n\n");
        return "user/dashboardUser"; // assure-toi que ce fichier existe dans templates/user/
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
