package com.gym.gym_application.service.impl;

import com.gym.gym_application.dto.*;
import com.gym.gym_application.dto.response.TraineeTrainingResponse;
import com.gym.gym_application.dto.response.TrainerTrainingResponse;
import com.gym.gym_application.exception.TraineeException;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.TrainingException;
import com.gym.gym_application.feign.*;
import com.gym.gym_application.model.Trainee;
import com.gym.gym_application.model.Trainer;
import com.gym.gym_application.model.Training;
import com.gym.gym_application.repository.TraineeRepository;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingRepository;
import com.gym.gym_application.service.interfaces.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainingServiceImpl implements TrainingService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    //private final ReportFeignClient reportFeignClient;
    private final KafkaTemplate<String, TrainingSummaryDto> kafkaReportsService;
    @Override
    public void addTraining(TrainingDto trainingDto) {
        log.info("TrainingServiceImpl : addTraining ");
        Trainee trainee = traineeRepository.findByUserUsername(trainingDto.getTraineeusername())
                .orElseThrow(() -> new TraineeException("Trainee does not exits."));
        Trainer trainer = trainerRepository
                .findByUserUsernameAndTrainingTypeTrainingTypeNameIgnoreCase(trainingDto.getTrainerusername(),
                        trainingDto.getTrainingtype())
                .orElseThrow(() -> new TrainerException(trainingDto.getTrainerusername() + " does not teach "
                        + trainingDto.getTrainingtype() + " or trainer does not exist."));
        Training training = trainingRepository.save(Training.builder().trainee(trainee).trainer(trainer)
                .trainingType(trainer.getTrainingType()).duration(trainingDto.getDuration())
                .trainingDate(trainingDto.getTrainingdate()).trainingName(trainingDto.getTrainingname()).build());
        trainer.getListOfTrainees().add(trainee);
        trainerRepository.save(trainer);
        kafkaReportsService.send("report-messages",
                TrainingSummaryDto.builder().trainerUsername(trainer.getUser().getUsername())
                        .trainerFirstname(trainer.getUser().getFirstname())
                        .trainerLastname(trainer.getUser().getLastname()).trainerStatus(trainer.getUser().isActive())
                        .date(training.getTrainingDate()).build());
//        reportFeignClient.addTrainingSummary(TrainingSummaryDto.builder().trainerUsername(trainer.getUser().getUsername())
//                .trainerFirstname(trainer.getUser().getFirstname()).trainerLastname(trainer.getUser().getLastname())
//                .trainerStatus(trainer.getUser().isActive()).date(training.getTrainingDate()).build());
    }

    @Override
    public List<TraineeTrainingResponse> getTraineeTrainingsList(String username, LocalDate periodFrom, LocalDate periodTo, String trainerName, String trainingType) {
        log.info("TrainingServiceImpl : getTraineesTrainingList ");
        return trainingRepository.getTraineeTrainingsList(username, periodFrom, periodTo, trainerName, trainingType)
                .stream()
                .map(training -> TraineeTrainingResponse.builder()
                        .trainingName(training.getTrainingName())
                        .trainerUsername(training.getTrainer().getUser().getUsername())
                        .trainingDate(training.getTrainingDate())
                        .trainingType(training.getTrainingType().getTrainingTypeName())
                        .trainingDuration(training.getDuration())
                        .build())
                .toList();
    }

    @Override
    public List<TrainerTrainingResponse> getTrainerTrainingsList(String username, LocalDate periodFrom, LocalDate periodTo, String traineeName) {
        log.info("TrainingServiceImpl : getTrainersTrainingList ");
        return trainingRepository.getTrainerTrainingsList(username,periodFrom,periodTo,traineeName)
                .stream()
                .map(training -> TrainerTrainingResponse.builder()
                        .trainingName(training.getTrainingName())
                        .trainingType(training.getTrainingType().getTrainingTypeName())
                        .trainingDate(training.getTrainingDate())
                        .trainingDuration(training.getDuration())
                        .traineeUsername(training.getTrainee().getUser().getUsername())
                        .build())
                .toList();
    }

    @Override
    public void deleteTraining(String trainingname) {
        Training training=trainingRepository.findByTrainingName(trainingname)
                .orElseThrow(()->new TrainingException("Training not found"));
        Trainer trainer=training.getTrainer();
        Trainee trainee=training.getTrainee();
        if(trainer.getListOfTrainees().contains(trainee)){
            trainer.getListOfTrainees().remove(trainee);
            trainerRepository.save(trainer);
        }
        if (trainee.getListOfTrainers().contains(trainer)) {
            trainee.getListOfTrainers().remove(trainer);
            traineeRepository.save(trainee);
        }
       trainingRepository.delete(training);
    }
}
