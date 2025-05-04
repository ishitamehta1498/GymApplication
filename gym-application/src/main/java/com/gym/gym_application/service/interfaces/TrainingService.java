package com.gym.gym_application.service.interfaces;

import com.gym.gym_application.dto.TrainingDto;
import com.gym.gym_application.dto.response.TraineeTrainingResponse;
import com.gym.gym_application.dto.response.TrainerTrainingResponse;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    void addTraining(TrainingDto trainingDto);

    List<TraineeTrainingResponse> getTraineeTrainingsList(String username, LocalDate periodFrom, LocalDate periodTo, String trainerName, String trainingType);

    List<TrainerTrainingResponse> getTrainerTrainingsList(String username, LocalDate periodFrom, LocalDate periodTo, String traineeName);

    void deleteTraining(String trainingname);
}
