package com.ensah.nlp_annotation.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/login?error=RoleInvalide"; // Par défaut
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            System.out.println("Rôle trouvé : " + role); // Log pour déboguer
            if (role.equals("ROLE_ADMIN_ROLE")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_ANNOTATEUR_ROLE")) {
                redirectUrl = "/user/dashboard"; // URL pour les annotateurs
                break;
            }
        }

        System.out.println("Redirection vers : " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}