package com.gym.gym_application.dto.response;

import com.gym.gym_application.dto.TraineeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatedTrainerResponse {
    private String username;
    private String firstname;
    private String lastname;
    private String specialization;
    private boolean isActive;
    private List<TraineeDto> listOfTrainees;
}
