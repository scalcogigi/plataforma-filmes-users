
package org.example.service;

import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository repository;
    @InjectMocks UserService service;

    @Test
    void deveCriarUsuarioERetornarRegistroSalvo() {
        User input = new User("auth0|1", "ana", "ana@example.com");
        User saved = new User("auth0|1", "ana", "ana@example.com");
        saved.setId("1");
        when(repository.save(input)).thenReturn(saved);

        assertThat(service.create(input)).isSameAs(saved);
        verify(repository).save(input);
    }

    @Test
    void deveBuscarUsuarioPorId() {
        User user = new User();
        when(repository.findById("1")).thenReturn(Optional.of(user));
        assertThat(service.findById("1")).isSameAs(user);
    }

    @Test
    void deveInformarIdQuandoUsuarioNaoExiste() {
        assertThatThrownBy(() -> service.findById("ausente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado: ausente");
    }

    @Test
    void deveListarUsuarios() {
        List<User> users = List.of(new User(), new User());
        when(repository.findAll()).thenReturn(users);
        assertThat(service.findAll()).containsExactlyElementsOf(users);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void deveAtualizarPerfilPreservandoIdentidadeRelacionamentosEDatas() {
        User existing = new User("auth0|original", "antigo", "antigo@example.com");
        existing.setId("1");
        existing.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        existing.setUpdateAt(Instant.parse("2026-02-01T00:00:00Z"));
        existing.setFilmesAssistidosIds(List.of("filme1"));
        existing.setFilmesFavoritosIds(List.of("filme2"));
        existing.setSeguidoresIds(List.of("user2"));
        existing.setSeguindoIds(List.of("user3"));
        User updated = new User("auth0|ignorado", "novo", "novo@example.com");
        updated.setDisplayName("Novo nome");
        updated.setProfileImageUrl("https://example.com/avatar.png");
        updated.setBio("Cinema");
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        User result = service.update("1", updated);

        assertThat(result).isSameAs(existing);
        assertThat(result.getUsername()).isEqualTo("novo");
        assertThat(result.getEmail()).isEqualTo("novo@example.com");
        assertThat(result.getDisplayName()).isEqualTo("Novo nome");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://example.com/avatar.png");
        assertThat(result.getBio()).isEqualTo("Cinema");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAuth0UserId()).isEqualTo("auth0|original");
        assertThat(result.getCreatedAt()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(result.getUpdateAt()).isEqualTo(Instant.parse("2026-02-01T00:00:00Z"));
        assertThat(result.getFilmesAssistidosIds()).containsExactly("filme1");
        assertThat(result.getFilmesFavoritosIds()).containsExactly("filme2");
        assertThat(result.getSeguidoresIds()).containsExactly("user2");
        assertThat(result.getSeguindoIds()).containsExactly("user3");
        verify(repository).save(existing);
    }

    @Test
    void naoDeveSalvarAtualizacaoDeUsuarioInexistente() {
        assertThatThrownBy(() -> service.update("ausente", new User()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveExcluirUsuarioEncontrado() {
        User user = new User();
        when(repository.findById("1")).thenReturn(Optional.of(user));
        service.delete("1");
        verify(repository).delete(user);
    }

    @Test
    void naoDeveExcluirUsuarioInexistente() {
        assertThatThrownBy(() -> service.delete("ausente"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any(User.class));
    }
}
