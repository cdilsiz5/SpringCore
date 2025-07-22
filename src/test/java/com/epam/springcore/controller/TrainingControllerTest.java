package com.epam.springcore.controller;


import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import com.epam.springcore.service.ITrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainingControllerTest {

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
    }

    @Test
    @DisplayName("POST /trainings - Success")
    void testCreateTraining_success() throws Exception {
        CreateTrainingRequest request = new CreateTrainingRequest(1L, 2L, "2025-07-01", 2L, 30);
        TrainingDto responseDto = TrainingDto.builder().id(1L).duration(30).build();

        when(trainingService.createTraining(any(CreateTrainingRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /trainings - Validation Error")
    void testCreateTraining_validationError() throws Exception {
        CreateTrainingRequest request = new CreateTrainingRequest();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /trainings/{id} - Success")
    void testGetTrainingById_success() throws Exception {
        TrainingDto trainingDto = TrainingDto.builder().id(1L).build();
        when(trainingService.getTraining(1L)).thenReturn(trainingDto);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /trainings/{id} - Not Found")
    void testGetTrainingById_notFound() throws Exception {
        when(trainingService.getTraining(99L)).thenThrow(new NotFoundException("Training not found"));

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"));
    }

    @Test
    @DisplayName("GET /trainings - All Trainings")
    void testGetAllTrainings_success() throws Exception {
        TrainingDto dto = TrainingDto.builder().id(1L).build();
        when(trainingService.getAllTrainings()).thenReturn(List.of(dto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainings/{id} - Update Success")
    void testUpdateTraining_success() throws Exception {
        UpdateTrainingRequest request = new UpdateTrainingRequest("1", "2", "2025-08-01", 2L, 45);
        TrainingDto dto = TrainingDto.builder().id(1L).duration(45).build();

        when(trainingService.updateTraining(eq(1L), any(UpdateTrainingRequest.class))).thenReturn(dto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value(45));
    }

    @Test
    @DisplayName("PUT /trainings/{id} - Not Found")
    void testUpdateTraining_notFound() throws Exception {
        UpdateTrainingRequest request = new UpdateTrainingRequest("1", "2", "2025-08-01", 2L, 45);

        when(trainingService.updateTraining(eq(99L), any(UpdateTrainingRequest.class)))
                .thenThrow(new NotFoundException("Training not found"));

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"));
    }

    @Test
    @DisplayName("DELETE /trainings/{id} - Success")
    void testDeleteTraining_success() throws Exception {
        doNothing().when(trainingService).deleteTraining(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /trainings/{id} - Not Found")
    void testDeleteTraining_notFound() throws Exception {
        doThrow(new NotFoundException("Training not found"))
                .when(trainingService).deleteTraining(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Training not found"));
    }
}