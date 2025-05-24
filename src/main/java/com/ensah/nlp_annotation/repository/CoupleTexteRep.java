package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.ClassePossible;
import com.ensah.nlp_annotation.entity.CoupleTexte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CoupleTexteRep extends JpaRepository<CoupleTexte, Integer> {
     Set<CoupleTexte> findByDatasetId(Long datasetId);
}
