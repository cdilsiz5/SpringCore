package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITrainerService trainerService;

    private TrainerDto mockTrainer;
    private UserDto mockUser;

    private static final String BASE_URL = "/api/epam/v1/trainers";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TrainerController controller = new TrainerController(trainerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockUser = UserDto.builder()
                .id(1L)
                .username("Cihan.Dilsiz")
                .firstName("Cihan")
                .lastName("Dilsiz")
                .userActive(true)
                .build();

        mockTrainer = TrainerDto.builder()
                .specialization("YOGA")
                .user(mockUser)
                .build();
    }

    @Test
    @DisplayName("GET /trainers/{username} - Success")
    void testGetTrainerByUsername_success() throws Exception {
        when(trainerService.getTrainerByUsername("Cihan.Dilsiz")).thenReturn(mockTrainer);

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainers/{username} - Not Found")
    void testGetTrainerByUsername_notFound() throws Exception {
        when(trainerService.getTrainerByUsername("unknown"))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc.perform(get(BASE_URL + "/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /trainers - Success")
    void testGetAllTrainers_success() throws Exception {
        when(trainerService.getAllTrainers()).thenReturn(List.of(mockTrainer));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainers/{username} - Success")
    void testUpdateTrainer_success() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setSpecialization(Specialization.CARDIO);

        mockTrainer.setSpecialization("CARDIO");

        when(trainerService.updateTrainer(eq("Cihan.Dilsiz"), any(UpdateTrainerRequest.class)))
                .thenReturn(mockTrainer);

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("CARDIO"));
    }

    @Test
    @DisplayName("PUT /trainers/{username} - Not Found")
    void testUpdateTrainer_notFound() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setSpecialization(Specialization.CARDIO);

        when(trainerService.updateTrainer(eq("notfound"), any(UpdateTrainerRequest.class)))
                .thenThrow(new NotFoundException("Trainer not found"));

        mockMvc.perform(put(BASE_URL + "/notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("DELETE /trainers/{username} - Success")
    void testDeleteTrainer_success() throws Exception {
        doNothing().when(trainerService).deleteTrainer("Cihan.Dilsiz");

        mockMvc.perform(delete(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /trainers/{username} - Not Found")
    void testDeleteTrainer_notFound() throws Exception {
        doThrow(new NotFoundException("Trainer not found"))
                .when(trainerService).deleteTrainer("notfound");

        mockMvc.perform(delete(BASE_URL + "/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainer not found"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /trainers/{username}/trainings - Success")
    void testGetTrainingHistory_success() throws Exception {
        TrainingDto training = TrainingDto.builder()
                .id(1L)
                .trainer(mockTrainer)
                .build();

        when(trainerService.getTrainingHistory(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz/trainings")
                        .param("from", "2025-07-21")
                        .param("to", "2025-12-31")
                        .param("traineeName", "Ali")
                        .param("traineeLastName", "Yılmaz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
