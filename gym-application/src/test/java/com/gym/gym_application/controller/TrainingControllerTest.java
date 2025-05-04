package com.gym.gym_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gym_application.dto.TrainingDto;
import com.gym.gym_application.dto.response.TraineeTrainingResponse;
import com.gym.gym_application.dto.response.TrainerTrainingResponse;
import com.gym.gym_application.service.interfaces.TrainingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainingService trainingService;
    @TestConfiguration
    static class Config {
        @Bean
        public TrainingService trainingService() {
            return Mockito.mock(TrainingService.class);
        }
    }

    @Test
    void testAddTraining() throws Exception {
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTraineeusername("testTrainee");
        trainingDto.setTrainerusername("testTrainer");
        trainingDto.setTrainingname("testTraining");
        trainingDto.setTrainingtype("testType");
        trainingDto.setDuration(10);
        doNothing().when(trainingService).addTraining(any(TrainingDto.class));
        mockMvc.perform(post("/training/addtraining")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDto)))
                .andExpect(status().isOk());
        verify(trainingService, times(1)).addTraining(any(TrainingDto.class));
    }

    @Test
    void testAddTrainingInvalidRequest() throws Exception {
       // empty DTO
        mockMvc.perform(post("/training/addtraining")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetTrainerTrainingsList() throws Exception {
        TrainerTrainingResponse response = new TrainerTrainingResponse();
        when(trainingService.getTrainerTrainingsList(anyString(), any(), any(), any()))
                .thenReturn(List.of(response));
        mockMvc.perform(get("/training/trainertrainingslist")
                        .param("trainerusername", "trainerUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(trainingService,times(1)).getTrainerTrainingsList(anyString(), any(), any(), any());
    }

    @Test
    void testGetTraineeTrainingsList() throws Exception {
        TraineeTrainingResponse response = new TraineeTrainingResponse();
        when(trainingService.getTraineeTrainingsList(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(response));
        mockMvc.perform(get("/training/traineetrainingslist")
                        .param("traineeusername", "traineeUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(trainingService,times(1)).getTraineeTrainingsList(anyString(), any(), any(), any(),any());
    }

    @Test
    void testDeleteTraining() throws Exception {
        doNothing().when(trainingService).deleteTraining(anyString());
        mockMvc.perform(delete("/training/deletetraining")
                        .param("trainingname", "Training Test name"))
                .andExpect(status().isOk());
        verify(trainingService, times(1)).deleteTraining("Training Test name");
    }
}