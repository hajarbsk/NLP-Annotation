package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date dataLimite;

    @ManyToOne
    private AppDataset dataset;

    @ManyToOne(optional = true)
    private Annotateur annotateur;

    @ManyToMany
    @JoinTable(
            name = "tache_couple_texte",
            joinColumns = @JoinColumn(name = "tache_id"),
            inverseJoinColumns = @JoinColumn(name = "couple_texte_id")
    )
    private List<CoupleTexte> coupleTexte;



    public boolean isCompleted() {
        return coupleTexte != null && !coupleTexte.isEmpty() &&
                coupleTexte.stream().allMatch(couple -> couple.getAnnotations() != null && !couple.getAnnotations().isEmpty());
    }
}
