package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.Annotateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface AnnotateurRep extends JpaRepository<Annotateur,Long> {
    Annotateur findAnnotateursById(Long id);

    Annotateur findAnnotateurByEmail(String email);




}
