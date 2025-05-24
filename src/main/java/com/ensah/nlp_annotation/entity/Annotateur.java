package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@Entity
@DiscriminatorValue("ANNOTATEUR")
public class Annotateur extends AppUser{

    @OneToMany(mappedBy = "annotateur")
    private Set<Annotation> annotations;

    @OneToMany(mappedBy = "annotateur")
    private Set<Tache> taches;
}
