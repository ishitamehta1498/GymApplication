package com.reports.ReportManagement.service;


import com.reports.ReportManagement.dto.TrainingSummaryDto;
import com.reports.ReportManagement.model.TrainingSummary;
import com.reports.ReportManagement.repository.TrainingSummaryRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TrainingSummaryServiceImpl implements TrainingSummaryService {

    private TrainingSummaryRepository trainingSummaryRepository;
    private ModelMapper modelMapper;

    @KafkaListener(topics="report-messages",groupId = "reportsapp")
    @Override
    public void addTrainingSummary(TrainingSummaryDto trainingSummaryDto) {
        if (trainingSummaryDto == null) {
            throw new IllegalArgumentException("TrainingSummaryDto cannot be null");
        }
        trainingSummaryRepository.insert(modelMapper.map(trainingSummaryDto, TrainingSummary.class));
    }

}
