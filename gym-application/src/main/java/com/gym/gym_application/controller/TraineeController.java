package com.gym.gym_application.controller;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TraineeRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTraineeRequest;
import com.gym.gym_application.dto.request.UpdateTraineesTrainerListRequest;
import com.gym.gym_application.dto.response.TraineeProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTraineeResponse;
import com.gym.gym_application.service.interfaces.TraineeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/trainee")
@RestController
public class TraineeController {
    private TraineeService traineeService;

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);

    @PostMapping("/registration")
    public ResponseEntity<Credentials> traineeRegistrartion(
            @RequestBody @Valid TraineeRegistrationRequest traineeRegistrationRequest) {
        log.info("TraineeController : traineeRegistration ");
        return new ResponseEntity<>(traineeService.traineeRegistration(traineeRegistrationRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable("username") String username) {
        log.info("TraineeController : getTraineeProfile ");
        return new ResponseEntity<>(traineeService.getTraineeProfile(username), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UpdatedTraineeResponse> updateTraineeProfile(
            @RequestBody @Valid UpdateTraineeRequest updateTraineeRequest) {
        log.info("TraineeController : updateTraineeProfile ");
        return new ResponseEntity<>(traineeService.updateTraineeProfile(updateTraineeRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable("username") String username) {
        log.info("TraineeController : deleteTraineeProfile ");
        traineeService.deleteTraineeProfile(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/updatetraineetrainers")
    public ResponseEntity<List<TrainerDto>> updateTraineesTrainers(
            @RequestBody @Valid UpdateTraineesTrainerListRequest updateTraineesTrainerListRequest) {
        log.info("TraineeController : updateTraineesTrainer ");
        return new ResponseEntity<>(traineeService.updateTraineesTrainers(updateTraineesTrainerListRequest), HttpStatus.OK);
    }

    @GetMapping("/nonactivetrainer/{username}")
    public ResponseEntity<List<TrainerDto>> getNonActiveTrainerList(@PathVariable("username") String username) {
        log.info("TraineeController : getNonActiveTrainerList ");
        return new ResponseEntity<>(traineeService.getNonActiveTrainersOnTrainee(username), HttpStatus.OK);

    }
}
