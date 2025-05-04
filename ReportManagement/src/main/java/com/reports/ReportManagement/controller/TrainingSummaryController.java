package com.reports.ReportManagement.controller;


import com.reports.ReportManagement.dto.TrainingSummaryDto;
import com.reports.ReportManagement.service.TrainingSummaryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/training-summary")
@RestController
public class TrainingSummaryController {
    private TrainingSummaryService trainingSummaryService;

    @PostMapping
    public ResponseEntity<Void> addTrainingSummary(@RequestBody TrainingSummaryDto trainingSummaryDto) {
        trainingSummaryService.addTrainingSummary(trainingSummaryDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
