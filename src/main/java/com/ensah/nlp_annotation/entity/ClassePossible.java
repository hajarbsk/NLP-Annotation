package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class ClassePossible {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String texteClasse;


    @ManyToOne
    @JoinColumn(name = "dataset_Id")
    private AppDataset dataset;

    @OneToMany(mappedBy = "classeChoisie")
    private List<Annotation> annotations;
}
