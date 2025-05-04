package com.gym.gym_application.repository;

import com.gym.gym_application.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training,Integer> {
    @Query("SELECT t FROM Training t " + "JOIN t.trainee tr " + "JOIN t.trainer tnr " + "JOIN t.trainingType tt "
            + "WHERE tr.user.username = :username "
            + "AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom) "
            + "AND (:periodTo IS NULL OR t.trainingDate <= :periodTo) "
            + "AND (:trainerName IS NULL OR tnr.user.username LIKE %:trainerName%) "
            + "AND (:trainingType IS NULL OR tt.trainingTypeName LIKE %:trainingType%)")
    List<Training> getTraineeTrainingsList(@Param("username") String username, @Param("periodFrom") LocalDate periodFrom,
                                           @Param("periodTo") LocalDate periodTo, @Param("trainerName") String trainerName,
                                           @Param("trainingType") String trainingType);

    @Query("SELECT t FROM Training t " + "JOIN t.trainee tr " + "JOIN t.trainer tnr " + "JOIN t.trainingType tt "
            + "WHERE tnr.user.username = :username "
            + "AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom) "
            + "AND (:periodTo IS NULL OR t.trainingDate <= :periodTo) "
            + "AND (:traineeName IS NULL OR tr.user.username LIKE %:traineeName%)")
    List<Training> getTrainerTrainingsList(@Param("username") String username, @Param("periodFrom") LocalDate periodFrom,
                                           @Param("periodTo") LocalDate periodTo, @Param("traineeName") String traineeName);

    void deleteAllByTraineeId(int id);

    Optional<Training> findByTrainingName(String trainingname);
}
