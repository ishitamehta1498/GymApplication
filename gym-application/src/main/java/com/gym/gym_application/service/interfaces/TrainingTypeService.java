package com.gym.gym_application.service.interfaces;

import com.gym.gym_application.dto.TrainingTypeDto;
import jakarta.validation.constraints.AssertFalse;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeDto> getAllTrainingTypes();
}
