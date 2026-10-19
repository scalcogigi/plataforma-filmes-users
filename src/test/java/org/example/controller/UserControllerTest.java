package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.support.MongoTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MongoTestConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveExecutarCicloCompletoDoUsuarioAutenticado()
            throws Exception {

        String response = mvc.perform(
                        put("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                          "username": "ana",
                                          "email": "ana@example.com",
                                          "displayName": "Ana",
                                          "profileImageUrl": "https://example.com/ana.png",
                                          "bio": "Cinema"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth0UserId")
                        .value("auth0|ana"))
                .andExpect(jsonPath("$.username").value("ana"))
                .andExpect(jsonPath("$.email")
                        .value("ana@example.com"))
                .andExpect(jsonPath("$.displayName")
                        .value("Ana"))
                .andExpect(jsonPath("$.profileImageUrl")
                        .value("https://example.com/ana.png"))
                .andExpect(jsonPath("$.bio").value("Cinema"))
                .andExpect(jsonPath("$.filmesAssistidosIds")
                        .isEmpty())
                .andExpect(jsonPath("$.filmesFavoritosIds")
                        .isEmpty())
                .andExpect(jsonPath("$.seguidoresIds")
                        .isEmpty())
                .andExpect(jsonPath("$.seguindoIds")
                        .isEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updateAt").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = mapper.readTree(response);
        String id = created.get("id").asText();

        assertThat(repository.findById(id)).isPresent();

        mvc.perform(
                        get("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.auth0UserId")
                        .value("auth0|ana"));

        mvc.perform(
                        get("/users/{id}", id)
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mvc.perform(
                        get("/users")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));

        mvc.perform(
                        put("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                          "username": "novo",
                                          "email": "novo@example.com",
                                          "displayName": "Novo nome",
                                          "profileImageUrl": "https://example.com/novo.png",
                                          "bio": "Filmes"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.auth0UserId")
                        .value("auth0|ana"))
                .andExpect(jsonPath("$.username").value("novo"))
                .andExpect(jsonPath("$.email")
                        .value("novo@example.com"))
                .andExpect(jsonPath("$.displayName")
                        .value("Novo nome"))
                .andExpect(jsonPath("$.bio").value("Filmes"));

        assertThat(repository.findById(id))
                .get()
                .extracting(User::getUsername)
                .isEqualTo("novo");

        mvc.perform(
                        delete("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ana")
                                ))
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());

        mvc.perform(
                        put("/users/me")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(status().isUnauthorized());

        mvc.perform(delete("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar404QuandoPerfilAutenticadoNaoExiste()
            throws Exception {

        mvc.perform(
                        get("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("auth0|ausente")
                                ))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Perfil do usuário autenticado não encontrado."
                        ))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}