package com.gym.gym_application.service;

import com.gym.gym_application.dto.TrainingDto;
import com.gym.gym_application.dto.TrainingSummaryDto;
import com.gym.gym_application.dto.response.TraineeTrainingResponse;
import com.gym.gym_application.dto.response.TrainerTrainingResponse;
import com.gym.gym_application.exception.TraineeException;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.TrainingException;
import com.gym.gym_application.model.*;
import com.gym.gym_application.repository.TraineeRepository;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingRepository;
import com.gym.gym_application.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private KafkaTemplate<String, TrainingSummaryDto> kafkaReportsService;
    @InjectMocks
    private TrainingServiceImpl trainingService;
    private Trainee trainee;
    private User user1,user2;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;
    @BeforeEach
    void setUp(){
        user1=new User(1, "Roopal", "Mehta", "roopal.mehta",
                "Password@1", "roopal.mehta@gmail.com", true, LocalDate.now());
        user2=new User(2, "Ishita", "Mehta", "ishita.mehta",
                "Password@2", "ishita.mehta@gmail.com", true, LocalDate.now());
        trainee = new Trainee(1,user1, LocalDate.now(),
                "Pal Link Road", new ArrayList<>());
        trainingType=new TrainingType("yoga");
        trainer= new Trainer(1,user2,trainingType,new ArrayList<>());
        training=new Training(1,trainee,trainer,"yoga1",trainingType,
                LocalDate.now(),1);
    }
    @Test
    void testAddTraining() {
        TrainingDto trainingDto=new TrainingDto("roopal.mehta","ishita.mehta","yoga1",
                LocalDate.now(),"yoga",1);
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsernameAndTrainingTypeTrainingTypeNameIgnoreCase("ishita.mehta","yoga"))
                .thenReturn(Optional.ofNullable(trainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        trainingService.addTraining(trainingDto);
        verify(kafkaReportsService,times(1)).send(eq("report-messages"),any(TrainingSummaryDto.class));
        verify(trainerRepository, times(1)).save(any(Trainer.class));
        verify(trainingRepository,times(1)).save(any(Training.class));
    }

    @Test
    void testAddTrainingWithInvalidTrainee() {
        TrainingDto trainingDto=new TrainingDto("roopal.mehta","ishita.mehta","yoga1",
                LocalDate.now(),"yoga",1);
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.empty());
        TraineeException ex= assertThrows(TraineeException.class, () -> trainingService.addTraining(trainingDto));
        assertEquals("Trainee does not exits.",ex.getMessage());
    }

    @Test
    void testAddTrainingWithInvalidTrainer() {
        TrainingDto trainingDto=new TrainingDto("roopal.mehta","ishita.mehta","yoga1",
                LocalDate.now(),"yoga",1);
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsernameAndTrainingTypeTrainingTypeNameIgnoreCase("ishita.mehta",
                "yoga")).thenReturn(Optional.empty());
        TrainerException ex= assertThrows(TrainerException.class, () -> trainingService.addTraining(trainingDto));
        assertEquals("ishita.mehta does not teach yoga or trainer does not exist.",ex.getMessage());
    }

    @Test
    void testGetTraineeTrainingsList(){
        TraineeTrainingResponse response=new TraineeTrainingResponse("yoga1", LocalDate.now(), "yoga", 1, "ishita.mehta");
        List<TraineeTrainingResponse> responseList = new ArrayList<>(List.of(response));
        when(trainingRepository.getTraineeTrainingsList("roopal.mehta", LocalDate.now(), LocalDate.now(), "ishita.mehta",
                "yoga")).thenReturn(new ArrayList<>(List.of(training)));
        List<TraineeTrainingResponse> traineeTrainingResponseList = trainingService.getTraineeTrainingsList("roopal.mehta", LocalDate.now(), LocalDate.now(), "ishita.mehta", "yoga");
        assertEquals(responseList.toString(),traineeTrainingResponseList.toString());
        assertEquals(1,traineeTrainingResponseList.size());
        assertEquals(1,traineeTrainingResponseList.get(0).getTrainingDuration());
    }

    @Test
    void testGetTrainerTrainingsList(){
        TrainerTrainingResponse response=new TrainerTrainingResponse("yoga1", LocalDate.now(), "yoga", 1, "roopal.mehta");
        List<TrainerTrainingResponse> responseList = new ArrayList<>(List.of(response));
        when(trainingRepository.getTrainerTrainingsList("ishita.mehta", LocalDate.now(), LocalDate.now(), "roopal.mehta"))
                .thenReturn(new ArrayList<>(List.of(training)));
        List<TrainerTrainingResponse> trainerTrainingResponseList = trainingService.getTrainerTrainingsList("ishita.mehta", LocalDate.now(), LocalDate.now(), "roopal.mehta");
        assertEquals(responseList.toString(),trainerTrainingResponseList.toString());
        assertEquals(1,trainerTrainingResponseList.size());
        assertEquals(1,trainerTrainingResponseList.get(0).getTrainingDuration());
    }
//check both the above methods with partial input.
    @Test
    void testDeleteTrainingWhenTrainingNotFound() {
        String trainingname="yoga";
        when(trainingRepository.findByTrainingName(trainingname))
                .thenReturn(Optional.empty());
        TrainingException ex = assertThrows(TrainingException.class,
                () -> trainingService.deleteTraining(trainingname));
        assertEquals("Training not found", ex.getMessage());
        verify(trainingRepository, times(1)).findByTrainingName(trainingname);
        verify(trainingRepository, never()).delete(any());
    }

    @Test
    void testDeleteTraining(){
        String trainingname="yoga";
        when(trainingRepository.findByTrainingName(trainingname)).thenReturn(Optional.ofNullable(training));
        trainingService.deleteTraining(trainingname);
        assertFalse(trainer.getListOfTrainees().contains(trainee));
        assertFalse(trainee.getListOfTrainers().contains(trainer));
        verify(traineeRepository,never()).save(any());
        verify(trainingRepository, times(1)).delete(training);
    }

    @Test
    void testDeleteTrainingTraineeTrainer(){
        String trainingname="yoga";
        trainee.getListOfTrainers().add(trainer);
        trainer.getListOfTrainees().add(trainee);
        when(trainingRepository.findByTrainingName(trainingname)).thenReturn(Optional.ofNullable(training));
        trainingService.deleteTraining(trainingname);
        verify(traineeRepository,times(1)).save(any());
        verify(trainerRepository,times(1)).save(any());
        verify(trainingRepository, times(1)).delete(training);
    }
}