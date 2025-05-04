package com.gym.gym_application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class TrainingSummaryDto {
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private boolean trainerStatus;
    private LocalDate date;
}