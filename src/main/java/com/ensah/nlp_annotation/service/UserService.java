package com.ensah.nlp_annotation.service;

import com.ensah.nlp_annotation.entity.Annotateur;
import com.ensah.nlp_annotation.entity.AppRole;
import com.ensah.nlp_annotation.entity.AppUser;
import com.ensah.nlp_annotation.repository.AnnotationRep;
import com.ensah.nlp_annotation.repository.AppRoleRep;
import com.ensah.nlp_annotation.repository.AppUserRep;
import com.ensah.nlp_annotation.repository.TacheRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private AppUserRep userRepo;
    @Autowired
    private AppRoleRep roleRepo; // À créer
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AnnotationRep annotationRep;
    @Autowired
    private TacheRep tacheRep;


    public void saveUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AppRole annotatorRole = roleRepo.findByRoleName("ROLE_ANNOTATEUR_ROLE")
                .orElseGet(() -> {
                    AppRole newRole = new AppRole();
                    newRole.setRoleName("ROLE_ANNOTATEUR_ROLE");
                    return roleRepo.save(newRole);
                });

        user.setRole(annotatorRole);
        user.setEnabled(true);
        userRepo.save(user);
    }

    public List<AppUser> getALlUsers() {
        AppRole annotatorRole = roleRepo.findByRoleName("ROLE_ANNOTATEUR_ROLE")
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_ANNOTATEUR_ROLE non trouvé"));
        List<AppUser> allUsers = userRepo.findAllByRole(annotatorRole);
        System.out.println("Utilisateurs avec ROLE_ANNOTATEUR_ROLE : " + allUsers);
        return allUsers;
    }

    public AppUser getUserById(Long id) {
        System.out.println("\n\n\nuser find  by id : " + id+"\n\n\n");
        return userRepo.findById(id).orElse(null);
    }

    public void deleteUserById(Long id) {
        AppUser user = userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        if (user instanceof Annotateur) {
            // Dissocier les annotations et tâches
            annotationRep.setAnnotatorToNullForUser(id);
            tacheRep.setAnnotatorToNullForUser(id);
        }
        userRepo.delete(user);
    }
    public void toggleUserEnabled(Long id) {
        AppUser user = userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        user.setEnabled(!user.isEnabled());
        userRepo.save(user);
    }
}