package org.example.strategy;

import org.example.entity.User;

import java.util.List;

public interface RecommendationStrategy {
    List<User> recommend(User currentUser, List<User> allUsers, int limit);
}
