package com.ensah.nlp_annotation.service;

import com.ensah.nlp_annotation.entity.*;

import java.util.Date;
import java.util.List;

public interface DatasetService {

     /**
      * Ajoute un dataset avec ses classes possibles et ses couples de texte.
      *
      * @param data       Le dataset à ajouter
      * @param classNames Liste des noms de classes possibles
      * @param couples    Liste des couples de texte associés
      */
     void addDataset(AppDataset data, List<String> classNames, List<CoupleTexte> couples);

     /**
      * Récupère tous les datasets disponibles.
      *
      * @return Liste des datasets
      */

     List<AppDataset> getAllDatasets();

     /**
      * Récupère un dataset par son identifiant.
      *
      * @param id Identifiant du dataset
      * @return Le dataset correspondant, ou null si non trouvé
      */
     AppDataset getDatasetById(Long id);

     /**
      * Récupère un annotateur par son identifiant.
      *
      * @param id Identifiant de l'annotateur
      * @return L'annotateur correspondant, ou null si non trouvé
      */
     Annotateur getAnnotateurById(Long id);

     /**
      * Ajoute un annotateur à un dataset avec une date limite pour la tâche.
      *
      * @param datasetId   Identifiant du dataset
      * @param annotatorId Identifiant de l'annotateur
      * @param deadline    Date limite pour la tâche
      */
     void addAnnotateur(Long datasetId, Long annotatorId, Date deadline);

     /**
      * Récupère tous les annotateurs disponibles.
      *
      * @return Liste des annotateurs
      */
     List<Annotateur> getAllAnnotators();

     /**
      * Affecte plusieurs annotateurs à un dataset.
      *
      * @param datasetId    Identifiant du dataset
      * @param annotatorIds Liste des identifiants des annotateurs
      */
     void affectAnnotatorsToDataset(Long datasetId, List<Long> annotatorIds,Date deadline);

     /**
      * Récupère les couples de texte associés à un dataset.
      *
      * @param datasetId Identifiant du dataset
      * @return Liste des couples de texte
      */
     List<CoupleTexte> getCouplesForDataset(Long datasetId);

     /**
      * Récupère les classes possibles associées à un dataset.
      *
      * @param datasetId Identifiant du dataset
      * @return Liste des classes possibles
      */
     List<ClassePossible> getClassesForDataset(Long datasetId);

     /**
      * Détache un annotateur d'un dataset en supprimant la tâche associée.
      *
      * @param datasetId   Identifiant du dataset
      * @param annotatorId Identifiant de l'annotateur
      */
     void detachAnnotatorFromDataset(Long datasetId, Long annotatorId);


     public List<Tache> getTasksByAnnotator(Long annotatorId) ;

     public Tache getTaskById(Long tacheId) ;
}