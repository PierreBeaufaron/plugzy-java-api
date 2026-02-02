package com.humanbooster.cda.plugzy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humanbooster.cda.plugzy.entity.User;
import com.humanbooster.cda.plugzy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void register_shouldReturn201_andCreateUser() throws Exception {
        // language=json
        String body = """
        {
          "username": "testuser",
          "email": "testuser@plugzy.test",
          "password": "password123",
          "phone": "0600000099"
        }
        """;

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("testuser@plugzy.test"))
                .andExpect(jsonPath("$.verified").value(false));

        Optional<User> created = userRepository.findByEmail("testuser@plugzy.test");
        assertThat(created).isPresent();
        assertThat(created.get().isVerified()).isFalse();
        // si tu stockes un code :
        assertThat(created.get().getVerificationCode()).isNotBlank();
    }

    @Test
    void verify_shouldReturn204_andSetVerifiedTrue_whenCodeIsCorrect() throws Exception {
        // 1) register
        String email = "verifyme@plugzy.test";
        String registerBody = """
        {
          "username": "verifyme",
          "email": "%s",
          "password": "password123",
          "phone": "0600000088"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // 2) récupère le code généré en BDD
        User user = userRepository.findByEmail(email).orElseThrow();
        String code = user.getVerificationCode();
        assertThat(code).isNotBlank();

        // 3) verify
        String verifyBody = """
        {
          "email": "%s",
          "code": "%s"
        }
        """.formatted(email, code);

        mockMvc.perform(post("/api/account/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyBody))
                .andExpect(status().isNoContent());

        // 4) check BDD
        User updated = userRepository.findByEmail(email).orElseThrow();
        assertThat(updated.isVerified()).isTrue();
    }

    @Test
    void verify_shouldReturn400_whenCodeIsWrong() throws Exception {
        // 1) register
        String email = "wrongcode@plugzy.test";
        String registerBody = """
        {
          "username": "wrongcode",
          "email": "%s",
          "password": "password123",
          "phone": "0600000077"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // 2) verify avec mauvais code
        String verifyBody = """
        {
          "email": "%s",
          "code": "000000"
        }
        """.formatted(email);

        mockMvc.perform(post("/api/account/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyBody))
                .andExpect(status().isBadRequest());

        // 3) check BDD (toujours false)
        User updated = userRepository.findByEmail(email).orElseThrow();
        assertThat(updated.isVerified()).isFalse();
    }
}
