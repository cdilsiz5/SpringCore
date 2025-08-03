package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.TrainingTypeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITrainingService trainingService;

    private static final String BASE_URL = "/api/epam/v1/trainings";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TrainingController controller = new TrainingController(trainingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should create a new training and return 201 Created")
    void testCreateTraining_success() throws Exception {
        CreateTrainingRequest request = new CreateTrainingRequest(1L, 2L, "2025-01-01", 1L, 45);
        TrainingDto response = new TrainingDto();

        when(trainingService.createTraining(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 400 Bad Request if training data is invalid")
    void testCreateTraining_validationError() throws Exception {
        CreateTrainingRequest request = new CreateTrainingRequest(null, null, "", null, 0);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return training by ID if found")
    void testGetTraining_success() throws Exception {
        when(trainingService.getTraining(1L)).thenReturn(new TrainingDto());

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 if training ID is not found")
    void testGetTraining_notFound() throws Exception {
        when(trainingService.getTraining(1L)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return all trainings")
    void testGetAllTrainings_success() throws Exception {
        when(trainingService.getAllTrainings()).thenReturn(List.of(new TrainingDto()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is unauthorized to get trainings")
    void testGetAllTrainings_unauthorized() throws Exception {
        when(trainingService.getAllTrainings()).thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should delete training by ID and return 204 No Content")
    void testDeleteTraining_success() throws Exception {
        doNothing().when(trainingService).deleteTraining(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 401 if user is unauthorized to delete training")
    void testDeleteTraining_unauthorized() throws Exception {
        doThrow(new UnauthorizedException("Unauthorized"))
                .when(trainingService).deleteTraining(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return all training types")
    void testGetAllTrainingTypes_success() throws Exception {
        when(trainingService.getAllTrainingTypes()).thenReturn(List.of(new TrainingTypeDto()));

        mockMvc.perform(get(BASE_URL + "/training-types"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is unauthorized to get training types")
    void testGetAllTrainingTypes_unauthorized() throws Exception {
        when(trainingService.getAllTrainingTypes())
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(get(BASE_URL + "/training-types"))
                .andExpect(status().isUnauthorized());
    }
}
