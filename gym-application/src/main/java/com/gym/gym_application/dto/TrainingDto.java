package com.gym.gym_application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDto {
    @NotBlank(message="trainee username is required")
    private String traineeusername;
    @NotBlank(message="trainer username is required")
    private String trainerusername;
    @NotBlank(message="training name is required")
    private String trainingname;
    @DateTimeFormat
    private LocalDate trainingdate;
    @NotBlank(message="training type is required")
    private String trainingtype;
    @NotNull
    @Min(value = 1, message = "duration must be at least 1 hour")
    private int duration;
}
