package com.gym.gym_application.controller;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TrainerRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTrainerRequest;
import com.gym.gym_application.dto.response.TrainerProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTrainerResponse;
import com.gym.gym_application.service.interfaces.TrainerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("/trainer")
@RestController
public class TrainerController {
    private TrainerService trainerService;

    @PostMapping("/registration")
    public ResponseEntity<Credentials> trainerRegistration(@Valid @RequestBody TrainerRegistrationRequest trainerRegistrationRequest){
        log.info("TrainerController : trainerRegistration ");
        return new ResponseEntity<>(trainerService.trainerRegistration(trainerRegistrationRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable("username") String username){
        log.info("TrainerController : getTrainerProfile ");
        return new ResponseEntity<>(trainerService.getTrainerProfile(username),HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<UpdatedTrainerResponse> updateTrainerProfile(@Valid @RequestBody  UpdateTrainerRequest updateTrainerRequest) {
        log.info("TrainerController : updateTrainerProfile ");
        return new ResponseEntity<>(trainerService.updateTrainerProfile(updateTrainerRequest), HttpStatus.OK);
    }

}
