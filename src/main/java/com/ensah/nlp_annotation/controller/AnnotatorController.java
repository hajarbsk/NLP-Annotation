package com.ensah.nlp_annotation.controller;

import com.ensah.nlp_annotation.entity.*;
import com.ensah.nlp_annotation.repository.AnnotationRep;
import com.ensah.nlp_annotation.repository.AppUserRep;
import com.ensah.nlp_annotation.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class AnnotatorController {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private AnnotationRep annotationRep;
    @Autowired
    private AppUserRep userRepo;

    @GetMapping("/dashboardUser")
    public String showAnnotatorDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long annotatorId = getAnnotatorIdFromAuth(auth);
        List<Tache> tasks = datasetService.getTasksByAnnotator(annotatorId);

        long completedTasksCount = tasks.stream()
                .filter(task -> task.getCoupleTexte() != null && !task.getCoupleTexte().isEmpty() &&
                        task.getCoupleTexte().stream().allMatch(couple -> couple.getAnnotations() != null && !couple.getAnnotations().isEmpty()))
                .count();
        long remainingTasksCount = tasks.size() - completedTasksCount;
        String percentageCompleted = tasks.isEmpty() ? "0" : String.valueOf((completedTasksCount * 100) / tasks.size());

        model.addAttribute("tasks", tasks);
        model.addAttribute("completedTasksCount", completedTasksCount);
        model.addAttribute("remainingTasksCount", remainingTasksCount);
        model.addAttribute("percentageCompleted", percentageCompleted);
        model.addAttribute("currentUser", getCurrentUser(auth));

        return "user/dashboardUser";
    }

    @GetMapping("/annotate/{tacheId}")
    public String showAnnotationPage(@PathVariable Long tacheId, Model model, Authentication authentication) {
        Tache tache = datasetService.getTaskById(tacheId);
        if (tache == null) {
            model.addAttribute("error", "Tâche non trouvée");
            return "redirect:/user/tasks";
        }

        String email = authentication.getName();
        AppUser user = userRepo.findByEmail(email);
        List<Annotation> annotations = annotationRep.findByAnnotateurId(user.getId());

        model.addAttribute("tache", tache);
        model.addAttribute("couples", tache.getCoupleTexte());
        model.addAttribute("annotations", annotations);
        model.addAttribute("classes", tache.getDataset().getClassePossibles().stream().map(ClassePossible::getTexteClasse).toList());
        return "user/travailler"; // Utilise un template dédié (à créer)
    }

    @PostMapping("/annotate/{tacheId}/{coupleId}")
    public String saveAnnotation(@PathVariable Long tacheId, @PathVariable Long coupleId,
                                 @RequestParam String label, Authentication authentication) {
        Tache tache = datasetService.getTaskById(tacheId);
        CoupleTexte couple = tache.getCoupleTexte().stream().filter(c -> c.getId().equals(coupleId)).findFirst().orElse(null);
        if (couple == null) return "redirect:/user/tasks";

        String email = authentication.getName();
        AppUser user = userRepo.findByEmail(email);
        Annotateur annotateur = (Annotateur) user;

        Annotation annotation = new Annotation();
        annotation.setCoupleTexte(couple);
        annotation.setAnnotateur(annotateur);
        annotation.setClasseChoisie(tache.getDataset().getClassePossibles().stream()
                .filter(cp -> cp.getTexteClasse().equals(label)).findFirst().orElse(null));
        annotationRep.save(annotation);

        return "redirect:/user/annotate/" + tacheId; // Redirige vers la même page d'annotation
    }

    @GetMapping("/tasks")
    public String showAnnotatorTasks(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long annotatorId = getAnnotatorIdFromAuth(auth);
        List<Tache> taches = datasetService.getTasksByAnnotator(annotatorId);
        model.addAttribute("taches", taches);
        return "user/tasks";
    }

    @GetMapping("/travailler/{tacheId}")
    public String workOnTask(@PathVariable Long tacheId, Model model) {
        Tache tache = datasetService.getTaskById(tacheId);
        if (tache == null) {
            model.addAttribute("error", "Tâche non trouvée avec l'ID : " + tacheId);
            return "redirect:/user/tasks";
        }
        model.addAttribute("tache", tache);
        return "redirect:/user/annotate/" + tacheId; // Redirige directement vers l'annotation
    }

    private Object getCurrentUser(Authentication auth) {
        return auth.getPrincipal(); // À améliorer avec un objet UserDetails si nécessaire
    }

    private Long getAnnotatorIdFromAuth(Authentication auth) {
        String email = auth.getName();
        AppUser user = userRepo.findByEmail(email);
        return user != null ? user.getId() : null; // Récupère l'ID réel
    }
}