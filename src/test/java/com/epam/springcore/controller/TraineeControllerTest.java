package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainer.TrainerUsernameRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.impl.TraineeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TraineeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITraineeService traineeService;

    private static final String BASE_URL = "/api/epam/v1/trainees";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TraineeController controller = new TraineeController(traineeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testCreateTrainee_success() throws Exception {
        CreateTraineeRequest request = new CreateTraineeRequest("Ali", "Veli", LocalDate.of(1990, 1, 1), "İstanbul");
        LoginCredentialsResponse response = new LoginCredentialsResponse("ali.veli", "123456");

        when(traineeService.createTrainee(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ali.veli"));
    }

    @Test
    void testCreateTrainee_validationError() throws Exception {
        CreateTraineeRequest request = new CreateTraineeRequest("", "", null, "");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTraineeByUsername_success() throws Exception {
        when(traineeService.getTraineeByUsername("ali.veli"))
                .thenReturn(new TraineeDto());

        mockMvc.perform(get(BASE_URL + "/ali.veli"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTraineeByUsername_notFound() throws Exception {
        when(traineeService.getTraineeByUsername("unknown"))
                .thenThrow(new NotFoundException("Trainee not found"));

        mockMvc.perform(get(BASE_URL + "/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllTrainees_success() throws Exception {
        when(traineeService.getAllTrainees()).thenReturn(List.of(new TraineeDto()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrainee_success() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest(LocalDate.of(1991, 1, 1), "Ankara");
        when(traineeService.updateTrainee(eq("ali.veli"), any())).thenReturn(new TraineeDto());

        mockMvc.perform(put(BASE_URL + "/ali.veli")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrainee_notFound() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest(LocalDate.of(1991, 1, 1), "Ankara");
        when(traineeService.updateTrainee(eq("unknown"), any())).thenThrow(new NotFoundException("Trainee not found"));

        mockMvc.perform(put(BASE_URL + "/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTrainee_success() throws Exception {
        doNothing().when(traineeService).deleteTrainee("ali.veli");

        mockMvc.perform(delete(BASE_URL + "/ali.veli"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTrainee_unauthorized() throws Exception {
        doThrow(new UnauthorizedException("Unauthorized")).when(traineeService).deleteTrainee("ali.veli");

        mockMvc.perform(delete(BASE_URL + "/ali.veli"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testToggleTraineeActivation_success() throws Exception {
        doNothing().when(traineeService).toggleActivation("ali.veli");

        mockMvc.perform(patch(BASE_URL + "/ali.veli/toggle-activation"))
                .andExpect(status().isOk());
    }

    @Test
    void testToggleTraineeActivation_notFound() throws Exception {
        doThrow(new NotFoundException("Trainee not found"))
                .when(traineeService).toggleActivation("unknown");

        mockMvc.perform(patch(BASE_URL + "/unknown/toggle-activation"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTrainingHistory_success() throws Exception {
        when(traineeService.getTrainingHistory(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new TrainingDto()));

        mockMvc.perform(get(BASE_URL + "/ali.veli/training-history")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUnassignedTrainers_success() throws Exception {
        when(traineeService.getUnassignedTrainers("ali.veli"))
                .thenReturn(List.of(new TrainerDto()));

        mockMvc.perform(get(BASE_URL + "/ali.veli/unassigned-trainers"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrainerList_success() throws Exception {
        TrainerUsernameRequest req = new TrainerUsernameRequest("trainer1");

        when(traineeService.updateTrainerList(eq("ali.veli"), any())).thenReturn(List.of(new TrainerDto()));

        mockMvc.perform(put(BASE_URL + "/ali.veli/update-trainer-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrainerList_notFound() throws Exception {
        TrainerUsernameRequest req = new TrainerUsernameRequest("unknown");

        when(traineeService.updateTrainerList(eq("ali.veli"), any()))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc.perform(put(BASE_URL + "/ali.veli/update-trainer-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isNotFound());
    }
}
