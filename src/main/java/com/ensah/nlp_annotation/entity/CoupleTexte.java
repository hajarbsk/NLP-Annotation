package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class CoupleTexte {



    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "couple_texte_seq")
    @SequenceGenerator(name = "couple_texte_seq", sequenceName = "couple_texte_seq", allocationSize = 1)
    private Long id;

    @Lob
    private String texte1;

    @Lob
    private String texte2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id")
    private AppDataset dataset;

    @OneToMany(mappedBy = "coupleTexte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Annotation> annotations = new HashSet<>();

    @ManyToMany(mappedBy = "coupleTexte")
    private Set<Tache> taches = new HashSet<>();
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoupleTexte that = (CoupleTexte) o;
        return id != null && id.equals(that.id);
    }
}
