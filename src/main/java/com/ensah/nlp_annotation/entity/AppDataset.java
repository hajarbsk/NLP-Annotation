package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class AppDataset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String datasetName;
    private String datasetDescription;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CoupleTexte> coupleTextes = new ArrayList<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ClassePossible> classePossibles = new HashSet<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tache> taches = new ArrayList<>();

    private double progress;

    public void calculateProgress() {
        if (coupleTextes == null || coupleTextes.isEmpty()) {
            this.progress = 0.0;
            return;
        }
        long annotatedCount = coupleTextes.stream()
                .filter(couple -> couple.getAnnotations() != null && !couple.getAnnotations().isEmpty())
                .count();
        this.progress = (int) ((annotatedCount * 100) / coupleTextes.size());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppDataset that = (AppDataset) o;
        return id != null && id.equals(that.id);
    }
}
