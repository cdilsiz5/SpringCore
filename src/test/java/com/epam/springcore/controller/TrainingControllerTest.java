package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.GymNotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.create.CreateTrainingRequest;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
        when(trainingService.createTraining(any(CreateTrainingRequest.class)))
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
        when(trainingService.getTraining("1")).thenReturn(mockTraining);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traineeId").value("10"));
    }

    @Test
    @DisplayName("GET /training - list all trainings")
    void testGetAllTrainings() throws Exception {
        List<TrainingDto> trainingList = Arrays.asList(mockTraining);
        when(trainingService.getAllTrainings()).thenReturn(trainingList);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /training/{id} - update training")
    void testUpdateTraining() throws Exception {
        when(trainingService.updateTraining(eq("1"), any(CreateTrainingRequest.class)))
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
    @Test
    @DisplayName("GET /training/{id} - Negative: not found should return 404")
    void testGetTrainingById_NotFound() throws Exception {
        when(trainingService.getTraining("999"))
                .thenThrow(new GymNotFoundException("Training not found"));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("GymNotFoundException"));
    }
    @Test
    @DisplayName("PUT /training/{id} - Negative: update non-existent training returns 404")
    void testUpdateTraining_NotFound() throws Exception {
        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTraineeId("10");
        request.setTrainerId("20");
        request.setDate("2025-07-15");
        request.setType("CROSSFIT");
        request.setDurationMinutes(60);

        when(trainingService.updateTraining(eq("999"), any(CreateTrainingRequest.class)))
                .thenThrow(new GymNotFoundException("Training not found"));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("GymNotFoundException"));
    }
    @Test
    @DisplayName("DELETE /training/{id} - Negative: not found should return 404")
    void testDeleteTraining_NotFound() throws Exception {
        doThrow(new GymNotFoundException("Training not found"))
                .when(trainingService).deleteTraining("999");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(delete(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("GymNotFoundException"));
    }

    @Test
    @DisplayName("POST /training - Negative: invalid request should return 400")
    void testCreateTraining_InvalidRequest() throws Exception {
        CreateTrainingRequest invalidRequest = new CreateTrainingRequest(); // boş request

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.validationErrors.traineeId").value("Trainee ID cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.trainerId").value("Trainer ID cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.date").value("Training date cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.type").value("Training type cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.durationMinutes").value("Training duration must be at least 1 minute"));
    }



}
