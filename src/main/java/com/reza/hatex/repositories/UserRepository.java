package com.reza.hatex.repositories;

import com.reza.hatex.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findByVerifiedToken(String token);
}
