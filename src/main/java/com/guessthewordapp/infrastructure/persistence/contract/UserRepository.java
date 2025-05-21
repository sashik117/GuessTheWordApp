package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);
    void delete(Long id);
}