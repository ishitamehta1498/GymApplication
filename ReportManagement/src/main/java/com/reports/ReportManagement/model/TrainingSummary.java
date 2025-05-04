package com.reports.ReportManagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "training_summary")
public class TrainingSummary {
    @Id
    private String id;
    private String trainerUsername;
    private String trainerFirstname;
    private String trainerLastname;
    private boolean trainerStatus;
    private LocalDate date;
}
