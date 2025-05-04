package com.gym.gym_application.service.interfaces;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TraineeRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTraineeRequest;
import com.gym.gym_application.dto.request.UpdateTraineesTrainerListRequest;
import com.gym.gym_application.dto.response.TraineeProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTraineeResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraineeService {
    Credentials traineeRegistration(TraineeRegistrationRequest traineeRegistrationRequest);

    TraineeProfileResponse getTraineeProfile(String username);

    UpdatedTraineeResponse updateTraineeProfile(UpdateTraineeRequest updateTraineeRequest);

    void deleteTraineeProfile(String username);

    List<TrainerDto> updateTraineesTrainers(UpdateTraineesTrainerListRequest updateTraineesTrainerListRequest);

    List<TrainerDto> getNonActiveTrainersOnTrainee(String username);
}
