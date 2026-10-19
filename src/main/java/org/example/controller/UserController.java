package org.example.controller;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public UserResponse syncCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserRequest request
    ) {
        User user =
                userService.syncAuthenticatedUser(
                        jwt.getSubject(),
                        request
                );

        return UserResponse.fromEntity(user);
    }

    @GetMapping("/me")
    public UserResponse findCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user =
                userService.findAuthenticatedUser(
                        jwt.getSubject()
                );

        return UserResponse.fromEntity(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.deleteAuthenticatedUser(
                jwt.getSubject()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public UserResponse findById(
            @PathVariable String id
    ) {
        return UserResponse.fromEntity(
                userService.findById(id)
        );
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}