
package org.example.repository;

import org.example.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByAuth0UserId(String auth0UserId);

    Optional<User> findByUsername(String username);
}
