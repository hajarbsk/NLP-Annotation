package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.AppDataset;
import com.ensah.nlp_annotation.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TacheRep extends JpaRepository<Tache, Long> {
    Optional<Tache> findByDatasetIdAndAnnotateurId(Long datasetId, Long annotatorId);
    List<Tache> findByAnnotateurId(Long annotatorId);
    List<Tache> findByDatasetId(Long datasetId);

    @Modifying
    @Query("UPDATE Tache t SET t.annotateur = null WHERE t.annotateur.id = :userId")
    void setAnnotatorToNullForUser(@Param("userId") Long userId);
}
