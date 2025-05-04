package com.gym.gym_application.controller;

import com.gym.gym_application.dto.TrainingDto;
import com.gym.gym_application.dto.response.TraineeTrainingResponse;
import com.gym.gym_application.dto.response.TrainerTrainingResponse;
import com.gym.gym_application.service.interfaces.TrainingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("/training")
@RestController
public class TrainingController {
    private TrainingService trainingService;

    @PostMapping("/addtraining")
    public ResponseEntity<Void> addTraining(@RequestBody @Valid TrainingDto trainingDto){
        log.info("TrainingController : addTraining ");
        trainingService.addTraining(trainingDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/traineetrainingslist")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainingsList(
            @RequestParam(value = "traineeusername") String username,
            @RequestParam(value = "periodfrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(value = "periodto", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(value = "trainername", required = false) String trainerName,
            @RequestParam(value = "trainingtype", required = false) String trainingType
    ){
        log.info("TrainingController : getTraineesTrainingsList ");
        return new ResponseEntity<>(
                trainingService.getTraineeTrainingsList(username, periodFrom, periodTo, trainerName, trainingType),
                HttpStatus.OK);

    }
    @GetMapping("/trainertrainingslist")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainingsList(
            @RequestParam("trainerusername") String username,
            @RequestParam(value = "periodfrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(value = "periodto", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(value = "traineename", required = false) String traineeName
    ){
        log.info("TrainingController : getTrainerTrainingsList ");
        return new ResponseEntity<>(
                trainingService.getTrainerTrainingsList(username, periodFrom, periodTo, traineeName), HttpStatus.OK);

    }

    @DeleteMapping("/deletetraining")
    public ResponseEntity<Void> deleteTraining( @RequestParam("trainingname") String trainingname) {
        log.info("TrainingController : deleteTraining ");
        trainingService.deleteTraining(trainingname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
