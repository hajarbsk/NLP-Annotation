package com.ensah.nlp_annotation.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends AppUser {

}
