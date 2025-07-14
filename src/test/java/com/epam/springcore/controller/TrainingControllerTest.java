package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.CreateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainingControllerTest {

    private MockMvc mockMvc;

    private TrainingController trainingController;

    @Mock
    private ITrainingService trainingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CreateTrainingRequest request;
    private TrainingDto mockTraining;

    private static final String BASE_URL = "/api/epam/v1/training";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainingController = new TrainingController(trainingService);
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();

        request = new CreateTrainingRequest();
        request.setTraineeId("10");
        request.setTrainerId("20");
        request.setDate("2025-07-15");
        request.setType("CROSSFIT");
        request.setDurationMinutes(60);

        mockTraining = new TrainingDto();
        mockTraining.setId("1");
        mockTraining.setTraineeId("10");
        mockTraining.setTrainerId("20");
        mockTraining.setDate("2025-07-15");
        mockTraining.setType("CROSSFIT");
        mockTraining.setDurationMinutes(60);
    }

    @Test
    @DisplayName("POST /training - create training")
    void testCreateTraining() throws Exception {
        Mockito.when(trainingService.createTraining(any(CreateTrainingRequest.class)))
                .thenReturn(mockTraining);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("CROSSFIT"));
    }

    @Test
    @DisplayName("GET /training/{id} - get training by id")
    void testGetTrainingById() throws Exception {
        Mockito.when(trainingService.getTraining("1")).thenReturn(mockTraining);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeId").value("10"));
    }

    @Test
    @DisplayName("GET /training - list all trainings")
    void testGetAllTrainings() throws Exception {
        List<TrainingDto> trainingList = Arrays.asList(mockTraining);
        Mockito.when(trainingService.getAllTrainings()).thenReturn(trainingList);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /training/{id} - update training")
    void testUpdateTraining() throws Exception {
        Mockito.when(trainingService.updateTraining(eq("1"), any(CreateTrainingRequest.class)))
                .thenReturn(mockTraining);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durationMinutes").value(60));
    }

    @Test
    @DisplayName("DELETE /training/{id} - delete training")
    void testDeleteTraining() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }
}
