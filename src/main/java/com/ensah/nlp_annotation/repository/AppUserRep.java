package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.AppRole;
import com.ensah.nlp_annotation.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRep extends JpaRepository<AppUser,Long>{
    AppUser findByEmail(String email);
    List<AppUser> findAllByRole(AppRole role);
}