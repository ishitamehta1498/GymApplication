package com.gym.gym_application.service.impl;

import com.gym.gym_application.dto.TrainingTypeDto;
import com.gym.gym_application.repository.TrainingTypeRepository;
import com.gym.gym_application.service.interfaces.TrainingTypeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private TrainingTypeRepository trainingTypeRepository;
    private ModelMapper modelMapper;

    @Override
    public List<TrainingTypeDto> getAllTrainingTypes() {
        log.info("TrainingTypeServiceImpl : GetAllTrainingTypes ");
        return trainingTypeRepository.findAll().stream().map(
                trainingType->modelMapper.map(trainingType,TrainingTypeDto.class)).toList();
    }

}