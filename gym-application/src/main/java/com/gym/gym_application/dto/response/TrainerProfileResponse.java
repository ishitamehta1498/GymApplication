package com.gym.gym_application.dto.response;

import com.gym.gym_application.dto.TraineeDto;
import com.gym.gym_application.dto.TrainerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrainerProfileResponse {
    private String firstname;
    private String lastname;
    private boolean isActive;
    private String specialization;
    private List<TraineeDto> listOfTrainees;
}
