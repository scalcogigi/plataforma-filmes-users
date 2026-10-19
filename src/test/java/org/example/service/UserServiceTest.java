package org.example.service;

import org.example.dto.UserRequest;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void deveCriarUsuarioUsandoIdentidadeDoToken() {
        UserRequest request = new UserRequest(
                "ana",
                "ana@example.com",
                "Ana",
                "https://example.com/ana.png",
                "Gosto de cinema"
        );

        when(repository.findByAuth0UserId("auth0|1"))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.syncAuthenticatedUser("auth0|1", request);

        assertThat(result.getAuth0UserId()).isEqualTo("auth0|1");
        assertThat(result.getUsername()).isEqualTo("ana");
        assertThat(result.getEmail()).isEqualTo("ana@example.com");
        assertThat(result.getDisplayName()).isEqualTo("Ana");
        assertThat(result.getProfileImageUrl())
                .isEqualTo("https://example.com/ana.png");
        assertThat(result.getBio()).isEqualTo("Gosto de cinema");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdateAt()).isNotNull();

        verify(repository).findByAuth0UserId("auth0|1");
        verify(repository).save(any(User.class));
    }

    @Test
    void deveAtualizarUsuarioAutenticadoExistente() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        User existing = new User(
                "auth0|1",
                "usuario-antigo",
                "antigo@example.com"
        );

        existing.setId("1");
        existing.setDisplayName("Nome antigo");
        existing.setBio("Bio antiga");
        existing.setCreatedAt(createdAt);
        existing.setUpdateAt(
                Instant.parse("2026-02-01T00:00:00Z")
        );

        existing.setFilmesAssistidosIds(List.of("filme1"));
        existing.setFilmesFavoritosIds(List.of("filme2"));
        existing.setSeguidoresIds(List.of("user2"));
        existing.setSeguindoIds(List.of("user3"));

        UserRequest request = new UserRequest(
                "usuario-novo",
                "novo@example.com",
                "Novo nome",
                "https://example.com/avatar.png",
                "Cinema"
        );

        when(repository.findByAuth0UserId("auth0|1"))
                .thenReturn(Optional.of(existing));

        when(repository.save(existing)).thenReturn(existing);

        User result = service.syncAuthenticatedUser(
                "auth0|1",
                request
        );

        assertThat(result).isSameAs(existing);
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAuth0UserId()).isEqualTo("auth0|1");
        assertThat(result.getUsername()).isEqualTo("usuario-novo");
        assertThat(result.getEmail()).isEqualTo("novo@example.com");
        assertThat(result.getDisplayName()).isEqualTo("Novo nome");
        assertThat(result.getProfileImageUrl())
                .isEqualTo("https://example.com/avatar.png");
        assertThat(result.getBio()).isEqualTo("Cinema");

        // Dados que não podem ser apagados ao editar o perfil
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getFilmesAssistidosIds())
                .containsExactly("filme1");
        assertThat(result.getFilmesFavoritosIds())
                .containsExactly("filme2");
        assertThat(result.getSeguidoresIds())
                .containsExactly("user2");
        assertThat(result.getSeguindoIds())
                .containsExactly("user3");

        verify(repository).save(existing);
    }

    @Test
    void naoDeveAceitarAuth0UserIdEnviadoNoCorpo() {
        UserRequest request = new UserRequest(
                "ana",
                "ana@example.com",
                "Ana",
                null,
                null
        );

        when(repository.findByAuth0UserId("auth0|verdadeiro"))
                .thenReturn(Optional.empty());

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.syncAuthenticatedUser(
                "auth0|verdadeiro",
                request
        );

        assertThat(result.getAuth0UserId())
                .isEqualTo("auth0|verdadeiro");
    }

    @Test
    void deveBuscarUsuarioAutenticado() {
        User user = new User(
                "auth0|1",
                "ana",
                "ana@example.com"
        );

        when(repository.findByAuth0UserId("auth0|1"))
                .thenReturn(Optional.of(user));

        User result = service.findAuthenticatedUser("auth0|1");

        assertThat(result).isSameAs(user);
    }

    @Test
    void deveInformarQuandoUsuarioAutenticadoNaoPossuiCadastro() {
        when(repository.findByAuth0UserId("auth0|ausente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.findAuthenticatedUser("auth0|ausente")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Perfil do usuário autenticado não encontrado.");
    }

    @Test
    void deveBuscarUsuarioPorId() {
        User user = new User();

        when(repository.findById("1"))
                .thenReturn(Optional.of(user));

        assertThat(service.findById("1")).isSameAs(user);
    }

    @Test
    void deveInformarIdQuandoUsuarioNaoExiste() {
        when(repository.findById("ausente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("ausente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado: ausente");
    }

    @Test
    void deveListarUsuarios() {
        List<User> users = List.of(
                new User(),
                new User()
        );

        when(repository.findAll()).thenReturn(users);

        assertThat(service.findAll())
                .containsExactlyElementsOf(users);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void deveExcluirUsuarioAutenticado() {
        User user = new User(
                "auth0|1",
                "ana",
                "ana@example.com"
        );

        when(repository.findByAuth0UserId("auth0|1"))
                .thenReturn(Optional.of(user));

        service.deleteAuthenticatedUser("auth0|1");

        verify(repository).delete(user);
    }

    @Test
    void naoDeveExcluirUsuarioAutenticadoInexistente() {
        when(repository.findByAuth0UserId("auth0|ausente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteAuthenticatedUser("auth0|ausente")
        ).isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any(User.class));
    }
}