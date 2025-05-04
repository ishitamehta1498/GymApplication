package com.gym.gym_application.service.impl;

import com.gym.gym_application.dto.*;
import com.gym.gym_application.dto.request.TraineeRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTraineeRequest;
import com.gym.gym_application.dto.request.UpdateTraineesTrainerListRequest;
import com.gym.gym_application.dto.response.TraineeProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTraineeResponse;
import com.gym.gym_application.exception.TraineeException;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.feign.*;
import com.gym.gym_application.model.Trainee;
import com.gym.gym_application.model.Trainer;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.TraineeRepository;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingRepository;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.interfaces.TraineeService;
import com.gym.gym_application.utilities.RandomPasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final KafkaTemplate<String, EmailDto> emailKafkaService;
    //private final EmailFeignClient emailFeignClient;
    @Value("${send.ccemail}")
    private String ccEmail;
    private String topic="email-messages";

    @Override
    public Credentials traineeRegistration(TraineeRegistrationRequest traineeRegistrationRequest) {
        log.info("TraineeServiceImpl : traineeRegistration ");
        log.info("{},{}",ccEmail,"email-messages");
        String username= traineeRegistrationRequest.getEmail().substring(0, traineeRegistrationRequest.getEmail().indexOf('@'));
        String password = RandomPasswordGenerator.generateRandomPassword();
        emailKafkaService.send(topic, EmailDto.builder().toEmail(traineeRegistrationRequest.getEmail())
                .ccEmail(ccEmail).subject("Trainee Registration Successful").remarks("Sent")
                .body("Hi " + traineeRegistrationRequest.getFirstname() + ",\nSuccessfully Registered..").build());
//        emailFeignClient.sendSimpleMailMessage(EmailDto.builder().toEmail(traineeRegistrationRequest.getEmail())
//                .ccEmail("mehtaishita98@gmail.com").subject("Trainee Registration Successful")
//                .remarks("Please reset the password.")
//                .body("Hi " + traineeRegistrationRequest.getFirstname() + ",\nSuccessfully Registered.").build());
        log.info("emailfeignclient");
        User user=userRepository.save(User.builder()
                .firstname(traineeRegistrationRequest.getFirstname())
                .lastname(traineeRegistrationRequest.getLastname())
                .email(traineeRegistrationRequest.getEmail())
                .username(username)
                .password(password)
                .build());
        traineeRepository.save(Trainee.builder()
                        .address(traineeRegistrationRequest.getAddress())
                .dateOfBirth(traineeRegistrationRequest.getDateOfBirth())
                        .user(user)
                .build());
        return Credentials.builder().username(username).password(password).build();
    }

    @Override
    public TraineeProfileResponse getTraineeProfile(String username) {
        log.info("TraineeServiceImpl : getTraineeProfile ");
        User user=userRepository.findByUsername(username).orElseThrow(
                ()->new UserException("User not found with username: "+username));
        Trainee trainee=traineeRepository.findByUserUsername(username).orElseThrow(
                ()->new TraineeException("Trainee not found with username: "+username));
        List<TrainerDto> listOfTrainerDto=trainee.getListOfTrainers().stream()
                .map(trainer-> TrainerDto.builder()
                        .username(trainer.getUser().getUsername())
                        .firstname(trainer.getUser().getFirstname())
                        .lastname(trainer.getUser().getLastname())
                        .specialization(trainer.getTrainingType().getTrainingTypeName())
                        .build())
                .toList();
        return new TraineeProfileResponse(user.getFirstname(),user.getLastname(),trainee.getDateOfBirth(),
                trainee.getAddress(),user.isActive(),listOfTrainerDto);
    }

    @Override
    public UpdatedTraineeResponse updateTraineeProfile(UpdateTraineeRequest updateTraineeRequest) {
        log.info("TraineeServiceImpl : updateTraineeProfile ");
        User user=userRepository.findByUsername(updateTraineeRequest.getUsername()).orElseThrow(
                ()->new UserException("User not found with username: "+updateTraineeRequest.getUsername()));
        user.setFirstname(updateTraineeRequest.getFirstname());
        user.setLastname(updateTraineeRequest.getLastname());
        user.setActive(updateTraineeRequest.isActive());
        userRepository.save(user);
        Trainee trainee=traineeRepository.findByUserUsername(user.getUsername())
                .orElseThrow(()->new TraineeException("Trainee not found."));
        trainee.setAddress(updateTraineeRequest.getAddress());
        trainee.setDateOfBirth(updateTraineeRequest.getDateOfBirth());
        traineeRepository.save(trainee);
        List<TrainerDto> listOfTrainerDto = trainee.getListOfTrainers().stream()
                .map(trainer -> TrainerDto.builder()
                        .username(trainer.getUser().getUsername())
                        .firstname(trainer.getUser().getFirstname())
                        .lastname(trainer.getUser().getLastname())
                        .specialization(trainer.getTrainingType().getTrainingTypeName())
                        .build())
                .toList();
        emailKafkaService.send(topic,
                EmailDto.builder().toEmail(updateTraineeRequest.getUsername() + "@gmail.com").ccEmail(ccEmail)
                        .subject("Trainee Updated").remarks("Please reset the password.")
                        .body("Hi " + updateTraineeRequest.getFirstname() + ",\n Updated Successfully.").build());
//        emailFeignClient.sendSimpleMailMessage(EmailDto.builder().toEmail(updateTraineeRequest.getUsername() + "@gmail.com")
//                .ccEmail("mehtaishita98@gmail.com").subject("Trainee Updated")
//                .remarks("Please reset the password.")
//                .body("Hi " + updateTraineeRequest.getFirstname() + ",\n Updated Successfully.").build());
        return new UpdatedTraineeResponse(updateTraineeRequest.getUsername(),user.getFirstname(),user.getLastname(),
                trainee.getDateOfBirth(),trainee.getAddress(),user.isActive(),listOfTrainerDto);
    }

    @Override
    @Transactional
    public void deleteTraineeProfile(String username) {
        log.info("TraineeServiceImpl : deleteTraineeProfile ");
        Trainee trainee = traineeRepository.findByUserUsername(username).orElseThrow(
                () -> new TraineeException("Trainee with username: " + username + " not found."));
        trainingRepository.deleteAllByTraineeId(trainee.getId());
        trainee.getListOfTrainers().forEach(
                trainer -> {
                    trainer.getListOfTrainees().remove(trainee);
                    trainerRepository.save(trainer);
                }
        );
        traineeRepository.delete(trainee);
        userRepository.deleteByUsername(username);
    }

    @Override
    public List<TrainerDto> updateTraineesTrainers(UpdateTraineesTrainerListRequest updateTraineesTrainerListRequest) {
        log.info("TraineeServiceImpl : updateTraineesTrainers ");
        Trainee trainee = traineeRepository.findByUserUsername(updateTraineesTrainerListRequest.getTraineeUsername())
                .orElseThrow(() -> new TraineeException("User Not Found"));
        trainee.getListOfTrainers().stream()
                .forEach(trainer -> {
                    trainer.getListOfTrainees().remove(trainee);
                    trainerRepository.save(trainer);
                });
        List<Trainer> trainersList = new ArrayList<>();
        List<TrainerDto> trainersDto = new ArrayList<>();
        updateTraineesTrainerListRequest.getListOfTrainerUsernames().stream()
                .forEach(tr->{
                    Trainer trainer=trainerRepository.findByUserUsername(tr).orElseThrow(
                            ()-> new TrainerException("No such user been assigned as trainer"));
                    trainersList.add(trainer);
                    trainer.getListOfTrainees().add(trainee);
                    trainerRepository.save(trainer);
                    trainersDto.add(TrainerDto.builder()
                            .username(tr)
                            .firstname(trainer.getUser().getFirstname())
                            .lastname(trainer.getUser().getLastname())
                            .specialization(trainer.getTrainingType().getTrainingTypeName())
                            .build());

                });
        traineeRepository.save(trainee);
        trainee.setListOfTrainers(trainersList);
        emailKafkaService.send(topic,
                EmailDto.builder().toEmail(trainee.getUser().getEmail()).ccEmail(ccEmail)
                        .subject("Trainee Updated").remarks("Please reset the password")
                        .body("Hi " + trainee.getUser().getFirstname() + ",\n Updated Trainees trainers Successfully.")
                        .build());
//        emailFeignClient.sendSimpleMailMessage(
//                EmailDto.builder().toEmail(trainee.getUser().getEmail()).ccEmail("mehtaishita98@gmail.com")
//                        .subject("Trainee Updated").remarks("Please reset the password")
//                        .body("Hi " + trainee.getUser().getFirstname() + ",\n Updated Trainees trainers Successfully.")
//                        .build());
        return trainersDto;
    }

    @Override
    public List<TrainerDto> getNonActiveTrainersOnTrainee(String username) {
        log.info("TraineeServiceImpl : getNonActiveTrainersOnTrainee ");
        Trainee trainee = traineeRepository.findByUserUsername(username).orElseThrow(
                () -> new TraineeException("Trainee with username: " + username + " not found."));
        return trainerRepository.findAll().stream()
                .filter(trainer -> !trainer.getListOfTrainees().contains(trainee))
                .map(trainer->TrainerDto.builder()
                        .firstname(trainer.getUser().getFirstname())
                        .lastname(trainer.getUser().getLastname())
                        .username(trainer.getUser().getUsername())
                        .specialization(trainer.getTrainingType().getTrainingTypeName())
                        .build())
                .toList();
    }

}
