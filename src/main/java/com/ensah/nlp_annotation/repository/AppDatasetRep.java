package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.AppDataset;
import com.ensah.nlp_annotation.entity.CoupleTexte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface AppDatasetRep extends JpaRepository<AppDataset, Long> {
    AppDataset findByDatasetName(String  datasetName);
    AppDataset findAppDatasetsById(Long id);
}
