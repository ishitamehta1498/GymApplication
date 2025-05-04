package com.gym.gym_application.service.interfaces;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TrainerRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTrainerRequest;
import com.gym.gym_application.dto.response.TrainerProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTrainerResponse;

import java.util.List;

public interface TrainerService {
    Credentials trainerRegistration(TrainerRegistrationRequest trainerRegistrationRequest);

    TrainerProfileResponse getTrainerProfile(String username);

    UpdatedTrainerResponse updateTrainerProfile(UpdateTrainerRequest updateTrainerRequest);

}
