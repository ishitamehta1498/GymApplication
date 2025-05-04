package com.gym.gym_application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDto {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "firstname is required..")
    private String firstname;
    @NotBlank(message = "lastname is required..")
    private String lastname;

}
