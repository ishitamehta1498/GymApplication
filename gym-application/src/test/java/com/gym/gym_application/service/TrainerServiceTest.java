package com.gym.gym_application.service;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.EmailDto;
import com.gym.gym_application.dto.request.TrainerRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTrainerRequest;
import com.gym.gym_application.dto.response.TrainerProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTrainerResponse;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.feign.EmailFeignClient;
import com.gym.gym_application.model.Trainee;
import com.gym.gym_application.model.Trainer;
import com.gym.gym_application.model.TrainingType;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingTypeRepository;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.impl.TrainerServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private EmailFeignClient emailFeignClient;
    @Mock
    private KafkaTemplate<String, EmailDto> emailKafkaService;
    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainee trainee;
    private User user;
	private Trainer trainer;
    private TrainingType trainingType;
    @BeforeEach
    void setUp(){
		user = new User(1, "Ishita", "Mehta", "ishita.mehta",
                "Password@1", "ishita.mehta@gmail.com", true, LocalDate.now());
        trainingType = new TrainingType("yoga");
        trainer = new Trainer(1,user, trainingType, new ArrayList<>());
        trainee = new Trainee(1,new User(2, "Roopal", "Mehta", "roopal.mehta",
                "Password@2", "roopal.mehta@gmail.com", true, LocalDate.now()),
                LocalDate.now(), "Pal Link Road",
                new ArrayList<>(List.of(trainer)));
        trainer.getListOfTrainees().add(trainee);
    }

    @Test
    void testGetTrainerProfile(){
        String username="ishita.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
        TrainerProfileResponse response=trainerService.getTrainerProfile(username);
        assertEquals("Ishita",response.getFirstname());
        assertEquals("yoga",response.getSpecialization());
        assertTrue(response.isActive());
        assertEquals(response.toString(), trainerService.getTrainerProfile(username).toString());
    }

    @Test
    void testGetTrainerProfileUserNotFound() {
        String username="ishita.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        UserException ex = assertThrows(UserException.class, () -> trainerService.getTrainerProfile(username));
        assertEquals("User not found with username: ishita.mehta", ex.getMessage());
    }

    @Test
	void testGetTrainerProfileWithInvalidTrainer() {
        String username="ishita.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());
		TrainerException ex=assertThrows(TrainerException.class,()->trainerService.getTrainerProfile("ishita.mehta"));
	    assertEquals("Trainer not found with username: ishita.mehta", ex.getMessage());
    }

    @Test
    void testTrainerRegistration(){
        TrainerRegistrationRequest request=new TrainerRegistrationRequest("Ishita","Mehta","ishita.mehta@gmail.com","yoga");
        when((userRepository.save(any(User.class)))).thenReturn(user);
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase(request.getSpecialization())).thenReturn(Optional.ofNullable(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        Credentials credentials=trainerService.trainerRegistration(request);
        assertEquals("ishita.mehta", credentials.getUsername());
        assertNotNull(credentials.getPassword());
        verify(emailKafkaService, times(1)).send(eq("email-messages"), any(EmailDto.class));
    }

    @Test
    void testTrainerRegistrationTrainingTypeNotPresent(){
        TrainerRegistrationRequest request=new TrainerRegistrationRequest("Ishita","Mehta","ishita.mehta@gmail.com","yoga");
        when((userRepository.save(any(User.class)))).thenReturn(user);
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase(request.getSpecialization())).thenReturn(Optional.empty());
        when(trainingTypeRepository.save(any(TrainingType.class))).thenReturn(trainingType);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        Credentials credentials=trainerService.trainerRegistration(request);
        assertEquals("ishita.mehta", credentials.getUsername());
        assertNotNull(credentials.getPassword());
        verify(emailKafkaService, times(1)).send(eq("email-messages"), any(EmailDto.class));
    }

    @Test
    void testUpdateTrainerProfile(){
        UpdateTrainerRequest request=new UpdateTrainerRequest("ishita.mehta","Ishita","Mehta","yoga",true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.findByUserUsername("ishita.mehta")).thenReturn(Optional.ofNullable(trainer));
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase(request.getSpecialization())).thenReturn(Optional.ofNullable(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        UpdatedTrainerResponse response = trainerService.updateTrainerProfile(request);
        assertEquals("Ishita", response.getFirstname());
        assertEquals("yoga", response.getSpecialization());
        assertEquals(1, response.getListOfTrainees().size());
        verify(emailKafkaService,times(1)).send(eq("email-messages"),any(EmailDto.class));
    }

    @Test
    void testUpdateTrainerProfileTrainingTypeNotPresent(){
        UpdateTrainerRequest request=new UpdateTrainerRequest("ishita.mehta","Ishita","Mehta","yoga",true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.findByUserUsername("ishita.mehta")).thenReturn(Optional.ofNullable(trainer));
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase(request.getSpecialization())).thenReturn(Optional.empty());
        when(trainingTypeRepository.save(any(TrainingType.class))).thenReturn(trainingType);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        UpdatedTrainerResponse response = trainerService.updateTrainerProfile(request);
        assertEquals("Ishita", response.getFirstname());
        assertEquals("yoga", response.getSpecialization());
        assertEquals(1, response.getListOfTrainees().size());
        verify(emailKafkaService,times(1)).send(eq("email-messages"),any(EmailDto.class));
    }

    @Test
    void testUpdateTrainerProfileUserNotFound() {
        UpdateTrainerRequest request=new UpdateTrainerRequest("ishita.mehta","Ishita","Mehta","yoga",true);
        when(userRepository.findByUsername("ishita.mehta")).thenReturn(Optional.empty());
        UserException ex = assertThrows(UserException.class, () ->
                trainerService.updateTrainerProfile(request));
        assertEquals("User Not Found.", ex.getMessage());
    }

    @Test
    void testUpdateTrainerProfileTrainerNotFound() {
        UpdateTrainerRequest request=new UpdateTrainerRequest("ishita.mehta","Ishita","Mehta","yoga",true);
        when(userRepository.findByUsername("ishita.mehta")).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(trainerRepository.findByUserUsername("ishita.mehta")).thenReturn(Optional.empty());
        TrainerException ex = assertThrows(TrainerException.class, () ->
                trainerService.updateTrainerProfile(request));
        assertEquals("Trainer not found.", ex.getMessage());
    }
}