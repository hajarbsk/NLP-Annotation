package com.ensah.nlp_annotation.controller;

import com.ensah.nlp_annotation.entity.AppDataset;
import com.ensah.nlp_annotation.entity.Annotateur;
import com.ensah.nlp_annotation.entity.CoupleTexte;
import com.ensah.nlp_annotation.service.DatasetService;
import com.ensah.nlp_annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/Manage-dataset")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private UserService userService;

    @GetMapping("/manage-dataset")
    public String showDataSetManage(Model model) {
        List<AppDataset> datasets = datasetService.getAllDatasets();
        model.addAttribute("datasets", datasets);
        return "admin/Manage-dataset/manage-dataset";
    }

    @GetMapping("/add-dataset")
    public String showAddDataset(Model model) {
        model.addAttribute("dataset", new AppDataset());
        return "admin/Manage-dataset/add-dataset";
    }

    @PostMapping("/add-dataset")
    public String uploadDataset(
            @RequestParam("datasetName") String name,
            @RequestParam("description") String description,
            @RequestParam("classes") String classes,
            @RequestParam("file") MultipartFile file,
            Model model) {

        // Vérification si le fichier est vide
        if (file.isEmpty()) {
            model.addAttribute("error", "Veuillez sélectionner un fichier.");
            return "admin/Manage-dataset/add-dataset";
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            model.addAttribute("error", "Seuls les fichiers CSV sont acceptés.");
            return "admin/Manage-dataset/add-dataset";
        }

        AppDataset dataset = new AppDataset();
        dataset.setDatasetName(name);
        dataset.setDatasetDescription(description);

        List<String> classList = Arrays.asList(classes.split(";"));
        List<CoupleTexte> couples = extractCoupleTexteFromCSV(file,dataset);

        datasetService.addDataset(dataset, classList, couples);

        return "redirect:/admin/Manage-dataset/manage-dataset";
    }

    private List<CoupleTexte> extractCoupleTexteFromCSV(MultipartFile file,AppDataset dataset) {
        List<CoupleTexte> couples = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            // Ignorer l'en-tête (id,t,h,value)
            br.readLine(); // Sauter la première ligne

            // Lire les lignes de données
            String line;
            while ((line = br.readLine()) != null) {
                // Séparer les colonnes en tenant compte des virgules (gérer les guillemets si nécessaire)
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (columns.length != 4) continue; // Ignorer les lignes mal formées

                // Extraire 't' (index 1) et 'h' (index 2)
                String text = columns[1].trim();
                String hypothesis = columns[2].trim();

                // Créer un objet CoupleTexte
                CoupleTexte couple = new CoupleTexte();
                couple.setTexte1(text);
                couple.setTexte2(hypothesis);
                couple.setDataset(dataset); // <<== C'est important
                couples.add(couple);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
        }
        return couples;
    }

    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        AppDataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            model.addAttribute("error", "Dataset non trouvé avec l'ID : " + id);
            return "admin/Manage-dataset/manage-dataset";
        }
        dataset.calculateProgress();
        model.addAttribute("dataset", dataset);
        return "admin/Manage-dataset/details";
    }

    @GetMapping("/affecter-annotateur/{id}")
    public String showAffecterAnnotateur(@PathVariable Long id, Model model) {
        AppDataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            model.addAttribute("error", "Dataset non trouvé avec l'ID : " + id);
            return "redirect:/admin/Manage-dataset/manage-dataset";
        }
        model.addAttribute("dataset", dataset);
        model.addAttribute("annotators", datasetService.getAllAnnotators());
        return "admin/Manage-dataset/liste-annotateur";
    }

    @PostMapping("/affecter-annotateur/{id}")
    public String saveAffectedAnnotateur(
            @PathVariable Long id,
            @RequestParam(value = "annotatorIds", required = false) Long[] annotatorIds,
            @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date deadline,
            Model model) {
        AppDataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            model.addAttribute("error", "Dataset non trouvé avec l'ID : " + id);
            return "redirect:/admin/Manage-dataset/manage-dataset";
        }

        if (annotatorIds != null && annotatorIds.length > 0) {
            List<Long> annotatorIdList = Arrays.asList(annotatorIds);
            datasetService.affectAnnotatorsToDataset(id, annotatorIdList, deadline);
        } else {
            model.addAttribute("error", "Aucun annotateur sélectionné.");
            model.addAttribute("dataset", dataset);
            model.addAttribute("annotators", datasetService.getAllAnnotators());
            return "admin/Manage-dataset/liste-annotateur";
        }

        return "redirect:/admin/Manage-dataset/details/" + id;
    }

    @GetMapping("/remove-annotator/{datasetId}/{annotatorId}")
    public String removeAnnotator(@PathVariable Long datasetId, @PathVariable Long annotatorId, Model model) {
        try {
            AppDataset dataset = datasetService.getDatasetById(datasetId);
            if (dataset == null) {
                model.addAttribute("error", "Dataset non trouvé avec l'ID : " + datasetId);
                return "redirect:/admin/Manage-dataset/details/" + datasetId;
            }
            Annotateur annotator = datasetService.getAnnotateurById(annotatorId);
            if (annotator == null) {
                model.addAttribute("error", "Annotateur non trouvé avec l'ID : " + annotatorId);
                return "redirect:/admin/Manage-dataset/details/" + datasetId;
            }
            datasetService.detachAnnotatorFromDataset(datasetId, annotatorId);
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur est survenue lors du détachement : " + e.getMessage());
        }
        return "redirect:/admin/Manage-dataset/details/" + datasetId;
    }
}