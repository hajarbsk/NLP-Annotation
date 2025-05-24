package com.ensah.nlp_annotation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {
    @GetMapping("/error")
    public String handleError(@RequestParam(value = "message", required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "Une erreur s'est produite.");
        return "error"; // Template Thymeleaf error.html
    }
}