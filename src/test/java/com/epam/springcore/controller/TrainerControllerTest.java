package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.CreateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;

    private TrainerDto mockTrainer;
    private CreateTrainerRequest request;

    private static final String BASE_URL = "/api/epam/v1/trainer";

    @BeforeEach
    void setUp() {
        request = new CreateTrainerRequest();
        request.setFirstName("Cihan");
        request.setLastName("Dilsiz");
        request.setSpecialty(TrainingType.YOGA);

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
        Mockito.when(trainerService.createTrainer(any(CreateTrainerRequest.class)))
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
        Mockito.when(trainerService.getTrainer("1")).thenReturn(mockTrainer);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainers - list all")
    void testGetAllTrainers() throws Exception {
        List<TrainerDto> trainers = Arrays.asList(mockTrainer);
        Mockito.when(trainerService.getAllTrainers()).thenReturn(trainers);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainers/{id} - update")
    void testUpdateTrainer() throws Exception {
        Mockito.when(trainerService.updateTrainer(eq("1"), any(CreateTrainerRequest.class)))
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
}
