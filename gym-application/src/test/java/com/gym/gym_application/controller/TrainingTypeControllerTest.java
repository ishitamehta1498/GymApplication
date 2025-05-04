package com.gym.gym_application.controller;

import com.gym.gym_application.dto.TrainingTypeDto;
import com.gym.gym_application.model.TrainingType;
import com.gym.gym_application.repository.TrainingTypeRepository;
import com.gym.gym_application.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {
    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetAllTrainingTypes() {
        TrainingType trainingType = new TrainingType(1, "yoga");
        TrainingTypeDto trainingTypeDto = new TrainingTypeDto(1, "yoga");
        List<TrainingType> listOfTrainingTypes = new ArrayList<>(List.of(trainingType));
        List<TrainingTypeDto> listOfTrainingTypeDtos = new ArrayList<>(List.of(trainingTypeDto));
        when(trainingTypeRepository.findAll()).thenReturn(listOfTrainingTypes);
        when(modelMapper.map(trainingType, TrainingTypeDto.class)).thenReturn(trainingTypeDto);
        assertEquals(listOfTrainingTypeDtos.size(), trainingTypeService.getAllTrainingTypes().size());
    }
}
