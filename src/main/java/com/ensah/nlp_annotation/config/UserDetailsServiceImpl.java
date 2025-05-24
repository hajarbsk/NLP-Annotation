package com.ensah.nlp_annotation.config;


import com.ensah.nlp_annotation.entity.AppUser;
import com.ensah.nlp_annotation.repository.AppUserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserRep userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("Utilisateur désactivé : " + email);
        }

        // Charger le rôle depuis la relation AppRole
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName());
        // Créer l'autorité à partir du rôle unique
         return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                List.of(authority)
        );
    }
}

