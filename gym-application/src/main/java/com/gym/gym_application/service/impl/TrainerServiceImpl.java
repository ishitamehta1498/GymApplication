package com.gym.gym_application.service.impl;

import com.gym.gym_application.dto.*;
import com.gym.gym_application.dto.request.TrainerRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTrainerRequest;
import com.gym.gym_application.dto.response.TrainerProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTrainerResponse;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.feign.*;
import com.gym.gym_application.model.Trainer;
import com.gym.gym_application.model.TrainingType;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingTypeRepository;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.interfaces.TrainerService;
import com.gym.gym_application.utilities.RandomPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final KafkaTemplate<String, EmailDto> emailKafkaService;
    //private final EmailFeignClient emailFeignClient;
    //private final PasswordEncoder passwordEncoder;
    @Value("${send.ccemail}")
    private String ccEmail;
    @Override
    public Credentials trainerRegistration(TrainerRegistrationRequest trainerRegistrationRequest) {
        log.info("TrainerServiceImpl : TrainerRegistration ");
        String username = trainerRegistrationRequest.getEmail()
                .substring(0, trainerRegistrationRequest.getEmail().indexOf('@'));
        String password = RandomPasswordGenerator.generateRandomPassword();
        //String hashedPassword=passwordEncoder.encode(password);
        emailKafkaService.send("email-messages", EmailDto.builder().toEmail(trainerRegistrationRequest.getEmail())
                .ccEmail(ccEmail).subject("Trainer Registration Successful").remarks("Please reset the password.")
                .body("Hi" + trainerRegistrationRequest.getFirstname() + ",\nSuccessfully Registered..").build());
//        emailFeignClient.sendSimpleMailMessage(EmailDto.builder().toEmail(trainerRegistrationRequest.getEmail())
//                .ccEmail("mehtaishita98@gmail.com").subject("Trainer Registration Successful")
//                .remarks("Please reset the password")
//                .body("Hi " + trainerRegistrationRequest.getFirstname() + ",\nSuccesfully Registered.").build());
        User user = userRepository.save(User.builder().firstname(trainerRegistrationRequest.getFirstname())
                .lastname(trainerRegistrationRequest.getLastname()).email(trainerRegistrationRequest.getEmail())
                .username(username).password(password)
               // .password(hashedPassword)
                .build());
        TrainingType trainingType = trainingTypeRepository
                .findByTrainingTypeNameIgnoreCase(trainerRegistrationRequest.getSpecialization())
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder()
                        .trainingTypeName(trainerRegistrationRequest.getSpecialization()).build()));
        trainerRepository.save(Trainer.builder().trainingType(trainingType).user(user).build());
        return Credentials.builder().username(username).password(password).build();
    }

    @Override
    public TrainerProfileResponse getTrainerProfile(String username) {
        log.info("TrainerServiceImpl : getTrainerProfile ");
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new UserException("User not found with username: "+username));
        Trainer trainer=trainerRepository.findByUserUsername(username)
                .orElseThrow(()->new TrainerException("Trainer not found with username: "+user.getUsername()));
        List<TraineeDto> listOfTrainees=trainer.getListOfTrainees().stream()
                .map(trainee -> TraineeDto.builder().username(trainee.getUser().getUsername())
                        .firstname(trainee.getUser().getFirstname()).lastname(trainee.getUser().getLastname())
                        .build())
                .toList();
        return new TrainerProfileResponse(user.getFirstname(),user.getLastname(), user.isActive(), trainer.getTrainingType().getTrainingTypeName(),listOfTrainees);
    }

    @Override
    public UpdatedTrainerResponse updateTrainerProfile(UpdateTrainerRequest updateTrainerRequest) {
        log.info("TrainerServiceImpl : updateTrainerProfile ");
        User user = userRepository.findByUsername(updateTrainerRequest.getUsername())
                .orElseThrow(() -> new UserException("User Not Found."));
        user.setActive(updateTrainerRequest.isActive());
        user.setFirstname(updateTrainerRequest.getFirstname());
        user.setLastname(updateTrainerRequest.getLastname());
        user = userRepository.save(user);
        Trainer trainer = trainerRepository.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new TrainerException("Trainer not found."));
        TrainingType trainingType = trainingTypeRepository
                .findByTrainingTypeNameIgnoreCase(updateTrainerRequest.getSpecialization()).orElseGet(
                        () -> trainingTypeRepository.save(new TrainingType(updateTrainerRequest.getSpecialization())));
        trainer.setTrainingType(trainingType);
        trainer = trainerRepository.save(trainer);
        List<TraineeDto> listOfTraineesDto = trainer.getListOfTrainees().stream()
                .map(trainee -> TraineeDto.builder().username(trainee.getUser().getUsername())
                        .firstname(trainee.getUser().getFirstname()).lastname(trainee.getUser().getLastname())
                        .build())
                .toList();
        emailKafkaService.send("email-messages",
                EmailDto.builder().toEmail(updateTrainerRequest.getUsername() + "@gmail.com").ccEmail(ccEmail)
                        .subject("Trainer Updated").remarks("Please reset the password.")
                        .body("Hi " + updateTrainerRequest.getFirstname() + ",\n Updated Successfully.").build());
//        emailFeignClient.sendSimpleMailMessage(EmailDto.builder().toEmail(updateTrainerRequest.getUsername() + "@gmail.com")
//                .ccEmail("mehtaishita98@gmail.com").subject("Trainer Updated")
//                .remarks("Please reset the password.")
//                .body("Hi " + updateTrainerRequest.getFirstname() + ",\n Updated Successfully.").build());
        return new UpdatedTrainerResponse(user.getUsername(), user.getFirstname(), user.getLastname(),
                trainer.getTrainingType().getTrainingTypeName(), user.isActive(), listOfTraineesDto);
    }
}
