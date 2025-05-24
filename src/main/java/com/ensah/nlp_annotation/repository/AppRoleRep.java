package com.ensah.nlp_annotation.repository;

import com.ensah.nlp_annotation.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRep extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByRoleName(String roleName);}
