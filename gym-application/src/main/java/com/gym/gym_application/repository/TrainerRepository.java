package com.gym.gym_application.repository;

import com.gym.gym_application.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer,Integer> {
    Optional<Trainer> findByUserUsername(String username);

    Optional<Trainer> findByUserUsernameAndTrainingTypeTrainingTypeNameIgnoreCase(String trainerusername, String trainingtype);
}
