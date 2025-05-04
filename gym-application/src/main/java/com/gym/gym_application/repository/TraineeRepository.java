package com.gym.gym_application.repository;

import com.gym.gym_application.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee,Integer> {
    Optional<Trainee> findByUserUsername(String username);
}
