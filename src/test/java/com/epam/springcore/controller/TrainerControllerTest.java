package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

public class TrainerControllerTest {

    private MockMvc mockMvc;

    private TrainerController trainerController;

    @Mock
    private ITrainerService trainerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TrainerDto mockTrainer;
    private CreateTrainerRequest request;

    private static final String BASE_URL = "/api/epam/v1/trainer";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainerController = new TrainerController(trainerService);
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();

        request = new CreateTrainerRequest();
        request.setFirstName("Cihan");
        request.setLastName("Dilsiz");
        request.setSpecialty(Specialization.YOGA);

        mockTrainer = new TrainerDto();
        mockTrainer.setId("1");
        mockTrainer.setFirstName("Cihan");
        mockTrainer.setLastName("Dilsiz");
        mockTrainer.setUsername("Cihan.Dilsiz");
        mockTrainer.setSpecialization("YOGA");
    }

    @Test
    @DisplayName("POST /trainers - success")
    void testCreateTrainer() throws Exception {
        when(trainerService.createTrainer(any(CreateTrainerRequest.class)))
                .thenReturn(mockTrainer);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Cihan"));
    }



    @Test
    @DisplayName("GET /trainers/{id} - success")
    void testGetTrainerById() throws Exception {
        when(trainerService.getTrainer("1")).thenReturn(mockTrainer);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainers - list all")
    void testGetAllTrainers() throws Exception {
        List<TrainerDto> trainers = Arrays.asList(mockTrainer);
        when(trainerService.getAllTrainers()).thenReturn(trainers);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainers/{id} - update")
    void testUpdateTrainer() throws Exception {
        when(trainerService.updateTrainer(eq("1"), any(CreateTrainerRequest.class)))
                .thenReturn(mockTrainer);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("DELETE /trainers/{id} - success")
    void testDeleteTrainer() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("GET /trainer/{id} - Negative: not found should return 404")
    void testGetTrainerById_NotFound() throws Exception {
        when(trainerService.getTrainer("999"))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("NotFoundException"));
    }


    @Test
    @DisplayName("DELETE /trainer/{id} - Negative: not found should return 404")
    void testDeleteTrainer_NotFound() throws Exception {
        doThrow(new NotFoundException("Trainer not found"))
                .when(trainerService).deleteTrainer("999");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(delete(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("NotFoundException"));
    }


    @Test
    @DisplayName("PUT /trainer/{id} - Negative: update non-existent trainer returns 404")
    void testUpdateTrainer_NotFound() throws Exception {
        CreateTrainerRequest request = new CreateTrainerRequest();
        request.setFirstName("Ali");
        request.setLastName("Yılmaz");
        request.setSpecialty(Specialization.YOGA);

        when(trainerService.updateTrainer(eq("999"), any(CreateTrainerRequest.class)))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("NotFoundException"));
    }

    @Test
    @DisplayName("POST /trainer - Negative: invalid request should return 400")
    void testCreateTrainer_InvalidRequest() throws Exception {
        CreateTrainerRequest invalidRequest = new CreateTrainerRequest(); // tüm alanlar null

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.validationErrors.firstName").value("First name cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.lastName").value("Last name cannot be blank"))
                .andExpect(jsonPath("$.validationErrors.specialty").value("Specialty must be specified"));
    }

}
