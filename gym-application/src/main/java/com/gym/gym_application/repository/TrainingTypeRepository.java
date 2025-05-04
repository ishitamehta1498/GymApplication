package com.gym.gym_application.repository;

import com.gym.gym_application.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType,Integer> {
    Optional<TrainingType> findByTrainingTypeNameIgnoreCase(String specialization);
}
