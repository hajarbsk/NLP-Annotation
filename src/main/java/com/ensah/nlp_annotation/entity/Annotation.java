package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "classe_choisie_Id")
    private ClassePossible classeChoisie;

    @ManyToOne
    @JoinColumn(name = "couple_texte_Id")
    private CoupleTexte coupleTexte;

    @ManyToOne
    @JoinColumn(name = "annotateur_Id")
    private Annotateur annotateur;
}
