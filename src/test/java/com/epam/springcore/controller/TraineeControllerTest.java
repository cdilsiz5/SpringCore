package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TraineeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ITraineeService traineeService;

    private ObjectMapper objectMapper;

    private TraineeDto mockTrainee;
    private CreateTraineeRequest validRequest;

    private static final String BASE_URL = "/api/epam/v1/trainee";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TraineeController traineeController = new TraineeController(traineeService);
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        validRequest = new CreateTraineeRequest();
        validRequest.setFirstName("Cihan");
        validRequest.setLastName("Dilsiz");
        validRequest.setDateOfBirth(LocalDate.of(1999, 1, 1));
        validRequest.setAddress("Mersin");

        mockTrainee = new TraineeDto();
        mockTrainee.setId("1");
        mockTrainee.setFirstName("Cihan");
        mockTrainee.setLastName("Dilsiz");
        mockTrainee.setDateOfBirth(LocalDate.of(1999, 1, 1));
        mockTrainee.setAddress("Mersin");
    }

    // --- POSITIVE TEST CASES ---

    @Test
    @DisplayName("POST /trainee - Positive: create trainee successfully")
    void testCreateTrainee_Positive() throws Exception {
        when(traineeService.createTrainee(any(CreateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Cihan"));
    }

    @Test
    @DisplayName("GET /trainee/{id} - Positive: trainee found by ID")
    void testGetTraineeById_Positive() throws Exception {
        when(traineeService.getTrainee("1")).thenReturn(mockTrainee);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainee - Positive: list all trainees")
    void testGetAllTrainees_Positive() throws Exception {
        when(traineeService.getAllTrainees()).thenReturn(Collections.singletonList(mockTrainee));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainee/{id} - Positive: update trainee successfully")
    void testUpdateTrainee_Positive() throws Exception {
        when(traineeService.updateTrainee(eq("1"), any(CreateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Mersin"));
    }

    @Test
    @DisplayName("DELETE /trainee/{id} - Positive: delete trainee successfully")
    void testDeleteTrainee_Positive() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    // --- NEGATIVE TEST CASES ---
    @Test
    @DisplayName("GET /trainee/{id} - Negative: trainee not found should return 404")
    void testGetTraineeById_NotFound() throws Exception {
        when(traineeService.getTrainee("404"))
                .thenThrow(new NotFoundException("Trainee not found"));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/api/epam/v1/trainee/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("NotFoundException"));
    }


    @Test
    @DisplayName("DELETE /trainee/{id} - Negative: trainee not found should return 404")
    void testDeleteTrainee_NotFound() throws Exception {
        doThrow(new NotFoundException("Trainee not found"))
                .when(traineeService).deleteTrainee("404");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(delete("/api/epam/v1/trainee/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.exceptionType").value("NotFoundException"));
    }


    @Test
    @DisplayName("POST /trainee - Negative: invalid request body (missing fields)")
    void testCreateTrainee_Negative_InvalidRequest() throws Exception {
        CreateTraineeRequest invalidRequest = new CreateTraineeRequest(); // All fields null

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /trainee - Negative: validation errors should return 400")
    void testCreateTrainee_InvalidRequest() throws Exception {
        CreateTraineeRequest invalidRequest = new CreateTraineeRequest(); // boş body

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(post("/api/epam/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.validationErrors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.validationErrors.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.validationErrors.address").value("Address is required"))
                .andExpect(jsonPath("$.validationErrors.dateOfBirth").value("Date of birth is required"));
    }

}
