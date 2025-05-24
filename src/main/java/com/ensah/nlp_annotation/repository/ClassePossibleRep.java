package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.AppDataset;
import com.ensah.nlp_annotation.entity.ClassePossible;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassePossibleRep extends JpaRepository<ClassePossible, Long> {
     List<ClassePossible> findByDatasetId(Long datasetId);
}
