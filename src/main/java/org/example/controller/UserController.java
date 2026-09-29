
package org.example.controller;

import org.example.dto.UpdateUserRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = new User(request.auth0UserId(), request.username(), request.email());
        user.setDisplayName(request.displayName());
        user.setProfileImageUrl(request.profileImageUrl());
        user.setBio(request.bio());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromEntity(userService.create(user)));
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable String id) {
        return UserResponse.fromEntity(userService.findById(id));
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request
    ) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setProfileImageUrl(request.profileImageUrl());
        user.setBio(request.bio());

        return UserResponse.fromEntity(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
