
package org.example.service;

import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(String id, User updatedUser) {
        User existingUser = findById(id);

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setDisplayName(updatedUser.getDisplayName());
        existingUser.setProfileImageUrl(updatedUser.getProfileImageUrl());
        existingUser.setBio(updatedUser.getBio());

        return userRepository.save(existingUser);
    }

    public void delete(String id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
