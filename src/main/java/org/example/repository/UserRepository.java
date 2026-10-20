
package org.example.repository;

import org.example.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByAuth0UserId(String auth0UserId);

    Optional<User> findByUsername(String username);

    List<User> findByIdIn(Iterable<String> ids);
}
