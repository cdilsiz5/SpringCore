package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TraineeControllerTest {

    private MockMvc mockMvc;

    private TraineeController traineeController;

    @Mock
    private ITraineeService traineeService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private TraineeDto mockTrainee;
    private CreateTraineeRequest request;

    private static final String BASE_URL = "/api/epam/v1/trainee";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        traineeController = new TraineeController(traineeService);
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();

        request = new CreateTraineeRequest();
        request.setFirstName("Cihan");
        request.setLastName("Dilsiz");
        request.setDateOfBirth(LocalDate.of(1999, 1, 1));
        request.setAddress("Mersin");

        mockTrainee = new TraineeDto();
        mockTrainee.setId("1");
        mockTrainee.setFirstName("Cihan");
        mockTrainee.setLastName("Dilsiz");
        mockTrainee.setDateOfBirth(LocalDate.of(1999, 1, 1));
        mockTrainee.setAddress("Mersin");
    }

    @Test
    @DisplayName("POST /trainee - success")
    void testCreateTrainee() throws Exception {
        Mockito.when(traineeService.createTrainee(any(CreateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Cihan"));
    }

    @Test
    @DisplayName("GET /trainee/{id} - success")
    void testGetTraineeById() throws Exception {
        Mockito.when(traineeService.getTrainee("1")).thenReturn(mockTrainee);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainee - list all")
    void testGetAllTrainees() throws Exception {
        List<TraineeDto> trainees = Arrays.asList(mockTrainee);
        Mockito.when(traineeService.getAllTrainees()).thenReturn(trainees);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainee/{id} - update")
    void testUpdateTrainee() throws Exception {
        Mockito.when(traineeService.updateTrainee(eq("1"), any(CreateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Mersin"));
    }

    @Test
    @DisplayName("DELETE /trainee/{id} - success")
    void testDeleteTrainee() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }
}
