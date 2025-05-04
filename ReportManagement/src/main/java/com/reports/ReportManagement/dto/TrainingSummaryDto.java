package com.reports.ReportManagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrainingSummaryDto {
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private boolean trainerStatus;
    private LocalDate date;
}
