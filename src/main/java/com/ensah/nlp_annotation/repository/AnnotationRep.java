package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnotationRep extends JpaRepository<Annotation, Long> {

    List<Annotation> findByAnnotateurId(Long id);

    @Modifying
    @Query("UPDATE Annotation a SET a.annotateur = null WHERE a.annotateur.id = :userId")
    void setAnnotatorToNullForUser(@Param("userId") Long userId);
}
