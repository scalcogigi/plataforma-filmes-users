package org.example.factory;

import org.example.dto.UserResponse;
import org.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseFactory {
    public UserResponse create(User user) {
        return UserResponse.fromEntity(user);
    }
}
