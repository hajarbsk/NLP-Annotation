package com.ensah.nlp_annotation.controller;


import com.ensah.nlp_annotation.entity.AppUser;
import com.ensah.nlp_annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/Manage-user/manage-user")
    public String manageUsers(Model model) {
        System.out.println("\n\nManage-user\n\n");
        List<AppUser> userList = userService.getALlUsers();
        System.out.println("Liste des utilisateurs : " + userList); // Log pour déboguer
        model.addAttribute("users", userList);
        return "admin/Manage-user/manage-user";
    }



    @GetMapping("/Manage-user/user-register")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String userManage(Model model) {
        model.addAttribute("user", new AppUser()); // <-- important pour le formulaire
        return "admin/Manage-user/user-register";
    }

    @PostMapping("/Manage-user/user-register")
    public String registerUser(@ModelAttribute("user") AppUser user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user);
            System.out.println("Utilisateur créé : " + user);
            return "redirect:/admin/Manage-user/manage-user";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé.");
            return "redirect:/admin/Manage-user/user-register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de l'enregistrement.");
            return "redirect:/admin/Manage-user/user-register";
        }
    }


    @GetMapping("/Manage-user/edit-user/{id}")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String showEditUser(@PathVariable Long id, Model model) {
        AppUser user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/Manage-user/edit-user"; // à créer
    }

    @PostMapping("/Manage-user/edit-user/{id}")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") AppUser user) {
        AppUser existingUser = userService.getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        userService.saveUser(existingUser);
        System.out.println("Utilisateur modifié : " + existingUser);
        return "redirect:/admin/Manage-user/manage-user";
    }

    @GetMapping("/Manage-user/delete-user/{id}")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/Manage-user/manage-user";
    }

    @PostMapping("/admin/toggle-user-enabled/{id}")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String toggleUserEnabled(@PathVariable Long id) {
        userService.toggleUserEnabled(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getALlUsers());
        return "admin/users";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }



}
