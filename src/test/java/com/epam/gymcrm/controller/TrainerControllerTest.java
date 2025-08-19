package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.exception.NotFoundException;
import com.epam.gymcrm.exception.UnauthorizedException;
import com.epam.gymcrm.exception.handler.GlobalExceptionHandler;
import com.epam.gymcrm.filter.TransactionIdFilter;
import com.epam.gymcrm.model.enums.Specialization;
import com.epam.gymcrm.request.trainer.CreateTrainerRequest;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import com.epam.gymcrm.service.ITrainerService;
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

class TrainerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITrainerService trainerService;

    private static final String BASE_URL = "/api/epam/v1/trainers";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TrainerController controller = new TrainerController(trainerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(new TransactionIdFilter())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should return trainer by username if exists")
    void testGetTrainerByUsername_success() throws Exception {
        when(trainerService.getTrainerByUsername("john.doe")).thenReturn(new TrainerDto());

        mockMvc.perform(get(BASE_URL + "/john.doe"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 if trainer username is not found")
    void testGetTrainerByUsername_notFound() throws Exception {
        when(trainerService.getTrainerByUsername("unknown"))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc.perform(get(BASE_URL + "/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized if trainer is not active")
    void testGetTrainerByUsername_unauthorized() throws Exception {
        when(trainerService.getTrainerByUsername("unauth"))
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(get(BASE_URL + "/unauth"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return all trainers")
    void testGetAllTrainers_success() throws Exception {
        when(trainerService.getAllTrainers()).thenReturn(List.of(new TrainerDto()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete trainer if authorized")
    void testDeleteTrainer_success() throws Exception {
        doNothing().when(trainerService).deleteTrainer("john.doe");

        mockMvc.perform(delete(BASE_URL + "/john.doe"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return unauthorized if trainer delete is not allowed")
    void testDeleteTrainer_unauthorized() throws Exception {
        doThrow(new UnauthorizedException("Unauthorized")).when(trainerService).deleteTrainer("john.doe");

        mockMvc.perform(delete(BASE_URL + "/john.doe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 404 if trying to delete unknown trainer")
    void testDeleteTrainer_notFound() throws Exception {
        doThrow(new NotFoundException("Trainer not found"))
                .when(trainerService).deleteTrainer("unknown");

        mockMvc.perform(delete(BASE_URL + "/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should toggle trainer's active status")
    void testToggleActivation_success() throws Exception {
        doNothing().when(trainerService).changeTrainerActivation("john.doe",true);

        mockMvc.perform(patch(BASE_URL + "/john.doe/change-activation")
                .param("activate", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 when toggling unknown trainer's activation")
    void testToggleTrainerActivation_notFound() throws Exception {
        doThrow(new NotFoundException("Trainer not found"))
                .when(trainerService).changeTrainerActivation(eq("unknown"), eq(false));

        mockMvc.perform(patch(BASE_URL + "/unknown/change-activation")
                        .param("activate", "false"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return training history for trainer")
    void testGetTrainingHistory_success() throws Exception {
        when(trainerService.getTrainingHistory(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new TrainingDto()));

        mockMvc.perform(get(BASE_URL + "/john.doe/trainings")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return unauthorized if trainer is not active for training history")
    void testGetTrainingHistory_unauthorized() throws Exception {
        when(trainerService.getTrainingHistory(eq("unauth"), any(), any(), any(), any()))
                .thenThrow(new UnauthorizedException("Unauthorized"));

        mockMvc.perform(get(BASE_URL + "/unauth/trainings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return bad request if trainer creation request is invalid")
    void testCreateTrainer_validationError() throws Exception {
        CreateTrainerRequest request = new CreateTrainerRequest("", "", null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request if trainer update request is invalid")
    void testUpdateTrainer_validationError() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest(null);

        mockMvc.perform(put(BASE_URL + "/ali.yilmaz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 if updating trainer who does not exist")
    void testUpdateTrainer_notFound() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest(Specialization.BOXING);
        when(trainerService.updateTrainer(eq("unknown"), any()))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc.perform(put(BASE_URL + "/unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
