
package org.example.dto;

import org.example.entity.User;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        String id,
        String auth0UserId,
        String username,
        String email,
        String displayName,
        String profileImageUrl,
        String bio,
        List<String> filmesAssistidosIds,
        List<String> filmesFavoritosIds,
        List<String> seguidoresIds,
        List<String> seguindoIds,
        Instant createdAt,
        Instant updateAt
) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getAuth0UserId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getProfileImageUrl(),
                user.getBio(),
                user.getFilmesAssistidosIds(),
                user.getFilmesFavoritosIds(),
                user.getSeguidoresIds(),
                user.getSeguindoIds(),
                user.getCreatedAt(),
                user.getUpdateAt()
        );
    }
}
