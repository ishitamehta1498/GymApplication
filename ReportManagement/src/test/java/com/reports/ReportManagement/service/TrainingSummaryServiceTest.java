package com.reports.ReportManagement.service;

import com.reports.ReportManagement.dto.TrainingSummaryDto;
import com.reports.ReportManagement.model.TrainingSummary;
import com.reports.ReportManagement.repository.TrainingSummaryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrainingSummaryServiceTest {
    @Mock
    private TrainingSummaryRepository trainingSummaryRepository;
    @Mock private ModelMapper modelMapper;
    @InjectMocks
    private  TrainingSummaryServiceImpl trainingSummaryService;

    @Test
    public void addTrainingSummaryTest(){
        TrainingSummaryDto trainingSummaryDto = new TrainingSummaryDto();
        TrainingSummary trainingSummary = new TrainingSummary();
        when(modelMapper.map(trainingSummaryDto, TrainingSummary.class)).thenReturn(trainingSummary);
        when(trainingSummaryRepository.insert(trainingSummary)).thenReturn(trainingSummary);
        trainingSummaryService.addTrainingSummary(trainingSummaryDto);
        verify(trainingSummaryRepository, times(1)).insert(trainingSummary);
    }

    @Test
    public void addTraningSummary_nullDto_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,
                ()->trainingSummaryService.addTrainingSummary(null));
    }

}