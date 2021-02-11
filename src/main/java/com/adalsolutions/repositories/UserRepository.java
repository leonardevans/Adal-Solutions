package com.adalsolutions.repositories;

import com.adalsolutions.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsById(int id);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
