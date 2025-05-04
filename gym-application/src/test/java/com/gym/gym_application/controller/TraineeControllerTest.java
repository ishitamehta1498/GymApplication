package com.gym.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TrainerDto;
import com.gym.gym_application.dto.request.TraineeRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTraineeRequest;
import com.gym.gym_application.dto.request.UpdateTraineesTrainerListRequest;
import com.gym.gym_application.dto.response.TraineeProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTraineeResponse;
import com.gym.gym_application.exception.TraineeException;
import com.gym.gym_application.service.interfaces.TraineeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TraineeService traineeService;

    @TestConfiguration
    static class Config {
        @Bean
        public TraineeService traineeService() {
            return Mockito.mock(TraineeService.class);
        }
    }

    @Test
    void testTraineeRegistrartion() throws Exception {
        TraineeRegistrationRequest traineeRegistrationRequest=new TraineeRegistrationRequest();
        traineeRegistrationRequest.setFirstname("TraineeFirst");
        traineeRegistrationRequest.setLastname("TraineeLast");
        traineeRegistrationRequest.setEmail("trainee@gmail.com");
        Credentials credentials=new Credentials("traineeUser","traineePass");
        when(traineeService.traineeRegistration(any(TraineeRegistrationRequest.class)))
                .thenReturn(credentials);
        mockMvc.perform(post("/trainee/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(traineeRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("traineeUser"))
                .andExpect(jsonPath("$.password").value("traineePass"));
        verify(traineeService,times(1)).traineeRegistration(any(TraineeRegistrationRequest.class));
    }

    @Test
    void testTraineeRegistrationWithInvalidInput() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(); // Missing required fields
        mockMvc.perform(post("/trainee/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTraineeProfile() throws Exception {
        TraineeProfileResponse traineeProfileResponse = new TraineeProfileResponse();
        when(traineeService.getTraineeProfile("traineeUser")).thenReturn(traineeProfileResponse);
        mockMvc.perform(get("/trainee/" + "traineeUser"))
                .andExpect(status().isOk());
        verify(traineeService,times(1)).getTraineeProfile("traineeUser");
    }

    @Test
    void testGetTraineeProfileWithTraineeException() throws Exception {
        when(traineeService.getTraineeProfile("traineeUser")).thenThrow(TraineeException.class);
        mockMvc.perform(get("/trainee/" + "traineeUser").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTraineeProfile() throws Exception {
        UpdateTraineeRequest updateTraineeRequest = new UpdateTraineeRequest();
        updateTraineeRequest.setUsername("traineeUser");
        updateTraineeRequest.setFirstname("traineeFirst");
        updateTraineeRequest.setLastname("traineeLast");
        updateTraineeRequest.setAddress("traineeAddress");
        UpdatedTraineeResponse updatedTraineeResponse = new UpdatedTraineeResponse();
        when(traineeService.updateTraineeProfile(any(UpdateTraineeRequest.class))).thenReturn(updatedTraineeResponse);
        mockMvc.perform(put("/trainee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTraineeRequest)))
                .andExpect(status().isOk());
        verify(traineeService,times(1)).updateTraineeProfile(updateTraineeRequest);
    }

    @Test
    void testDeleteTraineeProfile() throws Exception {
        doNothing().when(traineeService).deleteTraineeProfile("traineeUser");
        mockMvc.perform(delete("/trainee/" + "traineeUser"))
                .andExpect(status().isOk());
        verify(traineeService, times(1)).deleteTraineeProfile("traineeUser");
    }

    @Test
    void testUpdateTraineesTrainers() throws Exception {
        UpdateTraineesTrainerListRequest updateTraineesTrainerListRequest = new UpdateTraineesTrainerListRequest();
        updateTraineesTrainerListRequest.setTraineeUsername("traineeUser");
        updateTraineesTrainerListRequest.setListOfTrainerUsernames(Arrays.asList("trainerUser"));
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("trainer1");
        when(traineeService.updateTraineesTrainers(any(UpdateTraineesTrainerListRequest.class)))
                .thenReturn(List.of(trainerDto));
        mockMvc.perform(put("/trainee/updatetraineetrainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTraineesTrainerListRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(traineeService, times(1)).updateTraineesTrainers(updateTraineesTrainerListRequest);
    }

    @Test
    void testGetNonActiveTrainerList() throws Exception {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("inactiveTrainer");
        trainerDto.setFirstname("trainerFirst");
        trainerDto.setLastname("trainerLast");
        when(traineeService.getNonActiveTrainersOnTrainee("traineeUser"))
                .thenReturn(List.of(trainerDto));
        mockMvc.perform(get("/trainee/nonactivetrainer/traineeUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("inactiveTrainer"));
        verify(traineeService, times(1)).getNonActiveTrainersOnTrainee("traineeUser");
    }
}