package org.example.service;

import org.example.dto.UserRequest;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.example.strategy.RecommendationStrategy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RecommendationStrategy recommendationStrategy;

    public UserService(UserRepository userRepository, RecommendationStrategy recommendationStrategy) {
        this.userRepository = userRepository;
        this.recommendationStrategy = recommendationStrategy;
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

    public List<User> recommendations(String auth0UserId) {
        User current = findAuthenticatedUser(auth0UserId);
        return recommendationStrategy.recommend(current, userRepository.findAll(), 5);
    }

    public void follow(String auth0UserId, String targetId) {
        User follower = findAuthenticatedUser(auth0UserId);
        User target = findById(targetId);
        if (follower.getId().equals(target.getId())) return;
        List<String> following = ensureList(follower.getSeguindoIds());
        List<String> followers = ensureList(target.getSeguidoresIds());
        follower.setSeguindoIds(following);
        target.setSeguidoresIds(followers);
        addUnique(following, target.getId());
        addUnique(followers, follower.getId());
        follower.setUpdateAt(Instant.now());
        target.setUpdateAt(Instant.now());
        userRepository.save(follower);
        userRepository.save(target);
    }

    public void unfollow(String auth0UserId, String targetId) {
        User follower = findAuthenticatedUser(auth0UserId);
        User target = findById(targetId);
        List<String> following = ensureList(follower.getSeguindoIds());
        List<String> followers = ensureList(target.getSeguidoresIds());
        follower.setSeguindoIds(following);
        target.setSeguidoresIds(followers);
        following.removeIf(target.getId()::equals);
        followers.removeIf(follower.getId()::equals);
        follower.setUpdateAt(Instant.now());
        target.setUpdateAt(Instant.now());
        userRepository.save(follower);
        userRepository.save(target);
    }

    private static List<String> nonNull(List<String> ids) { return ids == null ? List.of() : ids; }
    private static List<String> ensureList(List<String> ids) { return ids == null ? new ArrayList<>() : ids; }
    private int followersCount(User user) { return new HashSet<>(nonNull(user.getSeguidoresIds())).size(); }
    private int followingCount(User user) { return new HashSet<>(nonNull(user.getSeguindoIds())).size(); }
    private int totalConnections(User user) { return followersCount(user) + followingCount(user); }
    private static void addUnique(List<String> ids, String id) { if (!ids.contains(id)) ids.add(id); }

    public void deleteAuthenticatedUser(
            String auth0UserId
    ) {
        User user =
                findAuthenticatedUser(auth0UserId);

        userRepository.delete(user);
    }
    
}
