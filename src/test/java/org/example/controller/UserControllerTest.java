
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MongoTestConfig.class)
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveExecutarCicloCompletoDeCadastroConsultaAtualizacaoEExclusao() throws Exception {
        String json = mvc.perform(post("/users").contentType("application/json").content("""
                {"auth0UserId":"auth0|ana","username":"ana","email":"ana@example.com",
                 "displayName":"Ana","profileImageUrl":"https://example.com/ana.png","bio":"Cinema"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auth0UserId").value("auth0|ana"))
                .andExpect(jsonPath("$.username").value("ana"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.displayName").value("Ana"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/ana.png"))
                .andExpect(jsonPath("$.bio").value("Cinema"))
                .andExpect(jsonPath("$.filmesAssistidosIds").isEmpty())
                .andExpect(jsonPath("$.filmesFavoritosIds").isEmpty())
                .andExpect(jsonPath("$.seguidoresIds").isEmpty())
                .andExpect(jsonPath("$.seguindoIds").isEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updateAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        JsonNode created = mapper.readTree(json);
        String id = created.get("id").asText();
        assertThat(repository.findById(id)).isPresent();

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(get("/users"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(put("/users/{id}", id).contentType("application/json").content("""
                {"username":"novo","email":"novo@example.com","displayName":"Novo nome",
                 "profileImageUrl":"https://example.com/novo.png","bio":"Filmes"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.auth0UserId").value("auth0|ana"))
                .andExpect(jsonPath("$.username").value("novo"))
                .andExpect(jsonPath("$.email").value("novo@example.com"))
                .andExpect(jsonPath("$.displayName").value("Novo nome"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/novo.png"))
                .andExpect(jsonPath("$.bio").value("Filmes"));
        assertThat(repository.findById(id)).get().extracting(User::getUsername).isEqualTo("novo");

        mvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        assertThat(repository.findById(id)).isEmpty();
        mvc.perform(get("/users")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void deveRetornar404AoConsultarAtualizarOuExcluirUsuarioInexistente() throws Exception {
        mvc.perform(get("/users/ausente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado: ausente"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
        mvc.perform(put("/users/ausente").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/users/ausente")).andExpect(status().isNotFound());
        assertThat(repository.count()).isZero();
    }
}
