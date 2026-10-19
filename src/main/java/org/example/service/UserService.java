package org.example.service;

import org.example.dto.UserRequest;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User syncAuthenticatedUser(
            String auth0UserId,
            UserRequest request
    ) {
        User user = userRepository
                .findByAuth0UserId(auth0UserId)
                .orElseGet(() ->
                        new User(
                                auth0UserId,
                                request.username(),
                                request.email()
                        )
                );

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setProfileImageUrl(request.profileImageUrl());
        user.setBio(request.bio());
        user.setUpdateAt(Instant.now());

        return userRepository.save(user);
    }

    public User findAuthenticatedUser(String auth0UserId) {
        return userRepository
                .findByAuth0UserId(auth0UserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Perfil do usuário autenticado não encontrado."
                        )
                );
    }

    public User findById(String id) {
        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado: " + id
                        )
                );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteAuthenticatedUser(
            String auth0UserId
    ) {
        User user =
                findAuthenticatedUser(auth0UserId);

        userRepository.delete(user);
    }
    
}