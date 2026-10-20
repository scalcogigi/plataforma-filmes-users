package org.example.controller;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.factory.UserResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserResponseFactory userResponseFactory;

    public UserController(
            UserService userService,
            UserResponseFactory userResponseFactory
    ) {
        this.userService = userService;
        this.userResponseFactory = userResponseFactory;
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

        return userResponseFactory.create(user);
    }

    @GetMapping("/me")
    public UserResponse findCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user =
                userService.findAuthenticatedUser(
                        jwt.getSubject()
                );

        return userResponseFactory.create(user);
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
        return userResponseFactory.create(
                userService.findById(id)
        );
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll()
                .stream()
                .map(userResponseFactory::create)
                .toList();
    }

    @GetMapping("/recommendations")
    public List<UserResponse> recommendations(@AuthenticationPrincipal Jwt jwt) {
        return userService.recommendations(jwt.getSubject()).stream()
                .map(userResponseFactory::create)
                .toList();
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        userService.follow(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        userService.unfollow(jwt.getSubject(), id);
        return ResponseEntity.noContent().build();
    }
}
