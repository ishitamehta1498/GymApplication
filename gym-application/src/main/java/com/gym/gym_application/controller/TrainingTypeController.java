package com.gym.gym_application.controller;

import com.gym.gym_application.dto.TrainingTypeDto;
import com.gym.gym_application.service.interfaces.TrainingTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/gym/trainingtype")
@AllArgsConstructor
@RestController
public class TrainingTypeController {

    private TrainingTypeService trainingTypeService;

    @GetMapping
    public List<TrainingTypeDto> getAllTrainingType() {
        log.info("TrainingTypeController : GetAllTrainingType");
        return trainingTypeService.getAllTrainingTypes();
    }

}