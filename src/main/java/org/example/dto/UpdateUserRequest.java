
package org.example.dto;

public record UpdateUserRequest(
        String username,
        String email,
        String displayName,
        String profileImageUrl,
        String bio
) {
}
