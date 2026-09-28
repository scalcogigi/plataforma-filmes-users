
package org.example.dto;

public record UserRequest(
        String auth0UserId,
        String username,
        String email,
        String displayName,
        String profileImageUrl,
        String bio
) {
}
