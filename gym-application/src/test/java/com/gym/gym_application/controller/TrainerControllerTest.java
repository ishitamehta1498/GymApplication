package com.gym.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gym_application.dto.Credentials;
import com.gym.gym_application.dto.TraineeDto;
import com.gym.gym_application.dto.request.TrainerRegistrationRequest;
import com.gym.gym_application.dto.request.UpdateTrainerRequest;
import com.gym.gym_application.dto.response.TrainerProfileResponse;
import com.gym.gym_application.dto.response.UpdatedTrainerResponse;
import com.gym.gym_application.exception.TrainerException;
import com.gym.gym_application.service.interfaces.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TrainerController.class)
class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainerService trainerService;
    @TestConfiguration
    static class Config {
        @Bean
        public TrainerService trainerService() {
            return Mockito.mock(TrainerService.class);
        }
    }

    @Test
    void testTrainerRegistration() throws Exception {
        TrainerRegistrationRequest trainerRegistrationRequest=new TrainerRegistrationRequest();
        trainerRegistrationRequest.setFirstname("TrainerFirst");
        trainerRegistrationRequest.setLastname("TrainerLast");
        trainerRegistrationRequest.setEmail("trainertest@gmail.com");
        Credentials credentials=new Credentials("trainerUser","trainerPass");
        when(trainerService.trainerRegistration(any(TrainerRegistrationRequest.class)))
                .thenReturn(credentials);
        mockMvc.perform(post("/trainer/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trainerRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("trainerUser"))
                .andExpect(jsonPath("$.password").value("trainerPass"));
        verify(trainerService, times(1)).trainerRegistration(any(TrainerRegistrationRequest.class));
    }

    @Test
    void testTrainerRegistrationWithRuntimeException() throws Exception {
        mockMvc.perform(post("/trainer/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTrainerProfile() throws Exception {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstname("TrainerFirst");
        response.setLastname("TrainerLast");
        response.setSpecialization("zumba");
        response.setActive(true);
        TraineeDto traineeDto=new TraineeDto("traineeUser","TraineeFirst","TraineeLast");
        response.setListOfTrainees(Arrays.asList(traineeDto));
        when(trainerService.getTrainerProfile("trainerUser")).thenReturn(response);
        mockMvc.perform(get("/trainer/trainerUser"))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname").value("TrainerFirst"));
        verify(trainerService, times(1)).getTrainerProfile("trainerUser");
    }

    @Test
    void testGetTrainerProfileWithTrainerException() throws Exception {
        when(trainerService.getTrainerProfile("testuser")).thenThrow(TrainerException.class);
        mockMvc.perform(get("/trainer/" + "testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTrainerProfile() throws Exception {
        UpdateTrainerRequest updateTrainerRequest=new UpdateTrainerRequest();
        updateTrainerRequest.setUsername("trainerUser");
        updateTrainerRequest.setFirstname("TrainerFirst");
        updateTrainerRequest.setLastname("TrainerLast");
        UpdatedTrainerResponse response=new UpdatedTrainerResponse();
        response.setUsername("trainerUser");
        response.setFirstname("TrainerFirst");
        response.setLastname("TrainerLast");
        response.setSpecialization("zumba");
        response.setActive(true);
        TraineeDto traineeDto=new TraineeDto("traineeUser","TraineeFirst","TraineeLast");
        response.setListOfTrainees(Arrays.asList(traineeDto));
        when(trainerService.updateTrainerProfile(any(UpdateTrainerRequest.class))).thenReturn(response);
        mockMvc.perform(put("/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateTrainerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("TrainerFirst"))
                .andExpect(jsonPath("$.specialization").value("zumba"))
                .andExpect(jsonPath("$.listOfTrainees[0].username").value("traineeUser"));
        verify(trainerService, times(1)).updateTrainerProfile(any(UpdateTrainerRequest.class));
    }
}