package org.example.dto;

public record UserRequest(
        String username,
        String email,
        String displayName,
        String profileImageUrl,
        String bio
) {
}