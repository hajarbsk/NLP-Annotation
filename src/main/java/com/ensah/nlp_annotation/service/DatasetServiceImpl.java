package com.ensah.nlp_annotation.service;

import com.ensah.nlp_annotation.entity.*;
import com.ensah.nlp_annotation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private AppDatasetRep appDatasetRep;

    @Autowired
    private AnnotateurRep annotateurRep;

    @Autowired
    private TacheRep tacheRep;

    @Autowired
    private ClassePossibleRep classePossibleRep;

    @Autowired
    private CoupleTexteRep coupleTexteRep;

    @Override
    @Transactional
    public void addDataset(AppDataset dataset, List<String> classNames, List<CoupleTexte> couples) {
        if (dataset == null) {
            throw new IllegalArgumentException("Dataset cannot be null");
        }

        // Sauvegarde initiale du dataset
        appDatasetRep.save(dataset);

        // Récupération des classes existantes liées au dataset
        Set<ClassePossible> classesExistantes = dataset.getClassePossibles();

        // Ajout des classes absentes (par rapport à classNames) à la collection du dataset
        if (classNames != null && !classNames.isEmpty()) {
            for (String nomClasse : classNames) {
                if (nomClasse == null || nomClasse.trim().isEmpty()) continue;

                boolean existe = classesExistantes.stream()
                        .anyMatch(c -> c.getTexteClasse().equalsIgnoreCase(nomClasse.trim()));

                if (!existe) {
                    ClassePossible classe = new ClassePossible();
                    classe.setTexteClasse(nomClasse.trim());
                    classe.setDataset(dataset);
                    dataset.getClassePossibles().add(classe);
                }
            }
        }

        // Sauvegarde des couples liés au dataset
        if (couples != null && !couples.isEmpty()) {
            for (CoupleTexte couple : couples) {
                if (couple != null) {
                    couple.setDataset(dataset);
                    coupleTexteRep.save(couple);
                }
            }
        }
        // Sauvegarder à nouveau le dataset avec ses classes (si cascade)
        appDatasetRep.save(dataset);
    }

    @Override
    public AppDataset getDatasetById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Dataset ID cannot be null");
        }
        Optional<AppDataset> datasetOptional = appDatasetRep.findById(id);
        return datasetOptional.orElseThrow(() -> new IllegalArgumentException("Dataset with ID " + id + " not found"));
    }

    @Override
    public Annotateur getAnnotateurById(Long id) {
        return annotateurRep.findById(id).orElse(null);
    }

    @Override
    public List<AppDataset> getAllDatasets() {
        List<AppDataset> datasets = appDatasetRep.findAll();
        return datasets != null ? new ArrayList<>(datasets) : new ArrayList<>();
    }

    @Override
    public List<Annotateur> getAllAnnotators() {
        return annotateurRep.findAll();
    }

    @Override
    @Transactional
    public void affectAnnotatorsToDataset(Long datasetId, List<Long> annotatorIds, Date deadline) {
        AppDataset dataset = getDatasetById(datasetId);
        List<Annotateur> annotators = annotateurRep.findAllById(annotatorIds);
        if (annotators.size() != annotatorIds.size()) {
            throw new IllegalArgumentException("Some annotators not found");
        }

        // Récupérer tous les couples du dataset
        List<CoupleTexte> allCouples = new ArrayList<>(dataset.getCoupleTextes());
        if (allCouples.isEmpty()) {
            throw new IllegalStateException("No couples found in dataset with ID " + datasetId);
        }

        // Mélanger aléatoirement les couples
        Collections.shuffle(allCouples, new Random());

        // Calculer le nombre de couples par annotateur
        int totalCouples = allCouples.size();
        int annotatorsCount = annotators.size();
        int couplesPerAnnotator = totalCouples / annotatorsCount;
        int remainingCouples = totalCouples % annotatorsCount;

        // Distribuer les couples
        int startIndex = 0;
        for (int i = 0; i < annotators.size(); i++) {
            Annotateur annotator = annotators.get(i);
            int couplesForThisAnnotator = couplesPerAnnotator + (remainingCouples > 0 ? 1 : 0);
            int endIndex = startIndex + couplesForThisAnnotator;

            // Extraire la sous-liste des couples pour cet annotateur
            List<CoupleTexte> annotatorCouples = allCouples.subList(startIndex, Math.min(endIndex, allCouples.size()));

            // Créer une tâche pour l'annotateur
            Tache tache = new Tache();
            tache.setDataset(dataset);
            tache.setAnnotateur(annotator);
            tache.setCoupleTexte(new ArrayList<>(annotatorCouples));
            tache.setDataLimite(deadline);
            tacheRep.save(tache);

            // Mettre à jour les indices
            startIndex = endIndex;
            if (remainingCouples > 0) {
                remainingCouples--;
            }
        }

        // Mettre à jour la liste des tâches du dataset
        dataset.getTaches().addAll(tacheRep.findByDatasetId(datasetId));
        appDatasetRep.save(dataset);
    }

    @Override
    @Transactional
    public void addAnnotateur(Long datasetId, Long annotatorId, Date deadline) {
        AppDataset dataset = getDatasetById(datasetId);
        Annotateur annotator = annotateurRep.findById(annotatorId)
                .orElseThrow(() -> new IllegalArgumentException("Annotator with ID " + annotatorId + " not found"));

        List<CoupleTexte> allCouples = new ArrayList<>(dataset.getCoupleTextes());
        Collections.shuffle(allCouples, new Random()); // Mélange aléatoire

        Tache tache = new Tache();
        tache.setDataset(dataset);
        tache.setAnnotateur(annotator);
        tache.setDataLimite(deadline);

        if (!allCouples.isEmpty()) {
            int couplesPerTask = allCouples.size();
            List<CoupleTexte> annotatorCouples = allCouples.subList(0, Math.min(couplesPerTask, allCouples.size()));
            tache.setCoupleTexte(new ArrayList<>(annotatorCouples));
        }

        tacheRep.save(tache);
    }

    @Override
    public List<CoupleTexte> getCouplesForDataset(Long datasetId) {
        AppDataset dataset = getDatasetById(datasetId);
        return new ArrayList<>(dataset.getCoupleTextes());
    }

    @Override
    public List<ClassePossible> getClassesForDataset(Long datasetId) {
        return classePossibleRep.findByDatasetId(datasetId);
    }

    @Override
    @Transactional
    public void detachAnnotatorFromDataset(Long datasetId, Long annotatorId) {
        Optional<Tache> tacheOptional = tacheRep.findByDatasetIdAndAnnotateurId(datasetId, annotatorId);
        tacheOptional.ifPresent(tache -> tacheRep.delete(tache));
    }



    @Override
    public List<Tache> getTasksByAnnotator(Long annotatorId) {
        return tacheRep.findByAnnotateurId(annotatorId);
    }

    @Override
    public Tache getTaskById(Long tacheId) {
        return tacheRep.findById(tacheId).orElse(null);
    }
}