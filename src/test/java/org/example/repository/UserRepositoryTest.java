
package org.example.repository;

import org.example.entity.User;
import org.example.support.MongoTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(MongoTestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired UserRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void devePersistirERestaurarPerfilCompleto() {
        User user = new User("auth0|1", "ana", "ana@example.com");
        user.setAuth0UserId("auth0|2");
        user.setDisplayName("Ana");
        user.setBio("Cinema");
        user.setProfileImageUrl("https://example.com/ana.png");
        user.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        user.setUpdateAt(Instant.parse("2026-02-01T00:00:00Z"));
        user.setFilmesAssistidosIds(List.of("filme1"));
        user.setFilmesFavoritosIds(List.of("filme2"));
        user.setSeguidoresIds(List.of("user2"));
        user.setSeguindoIds(List.of("user3"));

        User saved = repository.save(user);
        assertThat(saved.getId()).isNotBlank();
        assertThat(repository.findById(saved.getId())).get()
                .usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void deveBuscarPorAuth0EUsername() {
        User ana = repository.save(new User("auth0|ana", "ana", "ana@example.com"));
        repository.save(new User("auth0|bia", "bia", "bia@example.com"));

        assertThat(repository.findByAuth0UserId("auth0|ana")).get()
                .extracting(User::getId).isEqualTo(ana.getId());
        assertThat(repository.findByUsername("ana")).get()
                .extracting(User::getId).isEqualTo(ana.getId());
        assertThat(repository.findByAuth0UserId("inexistente")).isEmpty();
        assertThat(repository.findByUsername("inexistente")).isEmpty();
    }

    @Test
    void deveAtualizarEExcluirRegistroPersistido() {
        User user = repository.save(new User("auth0|1", "ana", "ana@example.com"));
        user.setUsername("novo");
        repository.save(user);
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findByUsername("ana")).isEmpty();
        assertThat(repository.findByUsername("novo")).isPresent();
        repository.delete(user);
        assertThat(repository.findById(user.getId())).isEmpty();
    }
}
