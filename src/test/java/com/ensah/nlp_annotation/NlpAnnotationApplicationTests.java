package com.ensah.nlp_annotation;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

@SpringBootTest
@AutoConfigureMockMvc
public class NlpAnnotationApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLoginWithCsrf() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "hajar@gmail.com")
                        .param("password", "hajar")
                        .with(csrf())) // Ajoute un jeton CSRF valide
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testLoginWithoutCsrf() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "hajar@gmail.com")
                        .param("password", "hajar"))
                .andExpect(status().isForbidden()); // Doit échouer sans CSRF
    }
}