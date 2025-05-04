package com.gym.gym_application.repository;

import com.gym.gym_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);

    boolean existsByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}
