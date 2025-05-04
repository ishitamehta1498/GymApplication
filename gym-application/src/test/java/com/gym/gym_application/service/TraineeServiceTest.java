package com.gym.gym_application.service;

import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.EmailDto;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TraineeRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTraineeRequest;
import com.gym.gym_application.dto.request.UpdateTraineesTrainerListRequest;
import com.gym.gym_application.dto.response.TraineeProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTraineeResponse;
import com.gym.gym_application.exception.TraineeException;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.exception.UserException;
import com.gym.gym_application.model.Trainee;
import com.gym.gym_application.model.Trainer;
import com.gym.gym_application.model.TrainingType;
import com.gym.gym_application.model.User;
import com.gym.gym_application.repository.TraineeRepository;
import com.gym.gym_application.repository.TrainerRepository;
import com.gym.gym_application.repository.TrainingRepository;
import com.gym.gym_application.repository.UserRepository;
import com.gym.gym_application.service.impl.TraineeServiceImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @InjectMocks
    private TraineeServiceImpl traineeService;
    @Mock
    private KafkaTemplate<String, EmailDto> emailKafkaService;

    private User user;
    private Trainee trainee;
    private Trainer trainer;
    @BeforeEach
    void setUp(){
        user=new User(2, "Roopal", "Mehta", "roopal.mehta",
                "Password@2", "roopal.mehta@gmail.com", true, LocalDate.now());
        trainee = new Trainee(1,user,
                LocalDate.now(), "Pal Link Road",
                new ArrayList<>());
        trainer = new Trainer(1,
                new User(1, "Ishita", "Mehta", "ishita.mehta",
                        "Password@1", "ishita.mehta@gmail.com", true, LocalDate.now()),
                new TrainingType("yoga"),
                new ArrayList<>());
        trainer.getListOfTrainees().add(trainee);
        trainee.getListOfTrainers().add(trainer);

    }

    @Test
    void testTraineeRegistration(){
        TraineeRegistrationRequest request=new TraineeRegistrationRequest("Roopal","Mehta",
                "roopal.mehta@gmail.com",LocalDate.now(),"Pal Link Road");
        when(userRepository.save(any(User.class))).thenReturn(user);
        Credentials credentials = traineeService.traineeRegistration(request);
        assertNotNull(credentials.getPassword());
        assertEquals("roopal.mehta",credentials.getUsername());
        verify(emailKafkaService,times(1)).send(eq("email-messages"),any(EmailDto.class));
        verify(traineeRepository,times(1)).save(any(Trainee.class));
    }

    @Test
    void testGetTraineeProfile(){
        String username="roopal.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.ofNullable(trainee));
        TraineeProfileResponse response = traineeService.getTraineeProfile(username);
        assertEquals("Roopal",response.getFirstname());
        assertEquals(LocalDate.now(),response.getDateOfBirth());
        assertTrue(response.isActive());
    }

    @Test
    void testGetTraineeProfileUserNotFound() {
        String username="roopal.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        UserException ex = assertThrows(UserException.class, () -> traineeService.getTraineeProfile(username));
        assertEquals("User not found with username: roopal.mehta", ex.getMessage());
    }

    @Test
    void testGetTrainerProfileWithInvalidTrainer() {
        String username="roopal.mehta";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        TraineeException ex=assertThrows(TraineeException.class,()->traineeService.getTraineeProfile("roopal.mehta"));
        assertEquals("Trainee not found with username: roopal.mehta", ex.getMessage());
    }

    @Test
    void testUpdateTraineeProfile(){
        UpdateTraineeRequest request=new UpdateTraineeRequest("roopal.mehta","Roopal","Mehta", LocalDate.now(),"Pal Link Road",true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.ofNullable(user));
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        UpdatedTraineeResponse response= traineeService.updateTraineeProfile(request);
        assertEquals("roopal.mehta",response.getUsername());
        assertEquals(1,response.getListOfTrainers().size());
        verify(userRepository,times(1)).save(any(User.class));
        verify(traineeRepository,times(1)).save(any(Trainee.class));
        verify(emailKafkaService,times(1)).send(eq("email-messages"),any(EmailDto.class));
    }

    @Test
    void testUpdateTraineeProfileUserNotFound() {
        UpdateTraineeRequest request=new UpdateTraineeRequest("roopal.mehta","Roopal","Mehta", LocalDate.now(),"Pal Link Road",true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        UserException ex = assertThrows(UserException.class, () -> traineeService.updateTraineeProfile(request));
        assertEquals("User not found with username: roopal.mehta", ex.getMessage());
    }

    @Test
    void testUpdateTraineeProfileWithInvalidTrainee() {
        UpdateTraineeRequest request=new UpdateTraineeRequest("roopal.mehta","Roopal","Mehta",
                LocalDate.now(),"Pal Link Road",true);
        when(userRepository.findByUsername("roopal.mehta")).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.empty());
        assertThrows(TraineeException.class, () -> traineeService.updateTraineeProfile(request));
    }

    @Test
    void testDeleteTraineeProfile(){
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        doNothing().when(trainingRepository).deleteAllByTraineeId(1);
		doNothing().when(traineeRepository).delete(trainee);
		doNothing().when(userRepository).deleteByUsername("roopal.mehta");
		traineeService.deleteTraineeProfile("roopal.mehta");
        verify(trainingRepository, times(1)).deleteAllByTraineeId(1);
        verify(traineeRepository,times(1)).delete(any(Trainee.class));
        verify(userRepository,times(1)).deleteByUsername("roopal.mehta");
    }

    @Test
	void testDeleteTraineeProfileWithInvalidUsername() {
		when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.empty());
        TraineeException ex = assertThrows(TraineeException.class, () -> traineeService.deleteTraineeProfile("roopal.mehta"));
        assertEquals("Trainee with username: roopal.mehta not found.",ex.getMessage());
	}
    
    @Test
    void testUpdateTraineesTrainers(){
        UpdateTraineesTrainerListRequest request=new UpdateTraineesTrainerListRequest("roopal.mehta",
                new ArrayList<>(List.of("ishita.mehta")));
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername("ishita.mehta")).thenReturn(Optional.ofNullable(trainer));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        List<TrainerDto> trainerDtos = traineeService.updateTraineesTrainers(request);
        assertEquals(1, trainerDtos.size());
        TrainerDto dto = trainerDtos.get(0);
        assertEquals("ishita.mehta", dto.getUsername());
        assertEquals("Ishita", dto.getFirstname());
        assertEquals("Mehta", dto.getLastname());
        assertEquals("yoga", dto.getSpecialization());
        verify(emailKafkaService,times(1)).send(eq("email-messages"),any(EmailDto.class));
        verify(traineeRepository, times(1)).findByUserUsername("roopal.mehta");
        verify(trainerRepository, times(1)).findByUserUsername("ishita.mehta");
        verify(trainerRepository, atLeastOnce()).save(any(Trainer.class));
        verify(traineeRepository, times(1)).save(trainee);
    }

	@Test
	void testUpdateTraineesTrainersWithInvalidTrainee() {
        UpdateTraineesTrainerListRequest request=new UpdateTraineesTrainerListRequest("roopal.mehta",
                new ArrayList<>(List.of("ishita.mehta")));
		when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.empty());
		assertThrows(TraineeException.class,
				() -> traineeService.updateTraineesTrainers(request));
	}

    @Test
    void testUpdateTraineesTrainersWithInvalidTrainer() {
        UpdateTraineesTrainerListRequest request=new UpdateTraineesTrainerListRequest("roopal.mehta",
                new ArrayList<>(List.of("ishita.mehta")));
        when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
        when(trainerRepository.findByUserUsername("ishita.mehta")).thenReturn(Optional.empty());
        assertThrows(TrainerException.class,
                () -> traineeService.updateTraineesTrainers(request));
    }

    @Test
	void testGetNonActiveTrainersOnTraineeWithInvalidTrainee() {
		when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.empty());
		assertThrows(TraineeException.class,()->traineeService.getNonActiveTrainersOnTrainee("roopal.mehta"));
	}

    @Test
	void testGetNonActiveTrainersOnTraineeWithEmptyTrainers() {
		when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
		when(trainerRepository.findAll()).thenReturn(new ArrayList<>());
		List<TrainerDto> listOfTrainerDtos = new ArrayList<>();
        List<TrainerDto> nonActiveTrainersOnTrainee = traineeService.getNonActiveTrainersOnTrainee("roopal.mehta");
        assertEquals(listOfTrainerDtos.toString(),nonActiveTrainersOnTrainee.toString());
        assertEquals(0,nonActiveTrainersOnTrainee.size());
	}

    @Test
	void testGetNonActiveTrainersOnTrainee() {
        User user2=new User(3,"Reena","Mehta","reena.mehta","Password@3","reena.mehta@gmail.com",true,LocalDate.now());
        TrainingType trainingType2=new TrainingType("yoga");
        Trainer trainer2= new Trainer(3,user2,
                trainingType2,new ArrayList<>());
		when(traineeRepository.findByUserUsername("roopal.mehta")).thenReturn(Optional.ofNullable(trainee));
		when(trainerRepository.findAll()).thenReturn(new ArrayList<>(List.of(trainer2)));
		List<TrainerDto> listOfTrainerDtos = new ArrayList<>(
				List.of(new TrainerDto("reena.mehta", "Reena", "Mehta", "yoga")));
        List<TrainerDto> nonActiveTrainersOnTrainee = traineeService.getNonActiveTrainersOnTrainee("roopal.mehta");
        assertEquals(1,nonActiveTrainersOnTrainee.size());
        assertEquals(listOfTrainerDtos.toString(),nonActiveTrainersOnTrainee.toString());
	}
}