package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TraineeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TraineeDto mockTrainee;
    private UserDto mockUser;

    @Mock
    private ITraineeService traineeService;

    private static final String BASE_URL = "/api/epam/v1/trainees";
    private static final String AUTH_USERNAME = "admin";
    private static final String AUTH_PASSWORD = "pass";

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

        mockUser = UserDto.builder()
                .username("Cihan.Dilsiz")
                .firstName("Cihan")
                .lastName("Dilsiz")
                .userActive(true)
                .build();

        mockTrainee = TraineeDto.builder()
                .address("İstanbul")
                .dateOfBirth(LocalDate.of(1999, 1, 1))
                .user(mockUser)
                .build();
    }

    @Test
    void testGetTraineeByUsername_success() throws Exception {
        when(traineeService.getTraineeByUsername(AUTH_USERNAME, AUTH_PASSWORD, "Cihan.Dilsiz"))
                .thenReturn(mockTrainee);

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Cihan.Dilsiz"))
                .andExpect(jsonPath("$.address").value("İstanbul"));
    }

    @Test
    void testGetTraineeByUsername_notFound() throws Exception {
        when(traineeService.getTraineeByUsername(AUTH_USERNAME, AUTH_PASSWORD, "notfound"))
                .thenThrow(new NotFoundException("Trainee not found"));

        mockMvc.perform(get(BASE_URL + "/notfound")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"));
    }

    @Test
    void testGetAllTrainees_success() throws Exception {
        when(traineeService.getAllTrainees(AUTH_USERNAME, AUTH_PASSWORD)).thenReturn(List.of(mockTrainee));

        mockMvc.perform(get(BASE_URL)
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testUpdateTrainee_success() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest(LocalDate.of(1998, 5, 5), "Ankara");
        mockTrainee.setAddress("Ankara");

        when(traineeService.updateTrainee(eq(AUTH_USERNAME), eq(AUTH_PASSWORD), eq("Cihan.Dilsiz"), any(UpdateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Ankara"));
    }

    @Test
    void testDeleteTrainee_success() throws Exception {
        doNothing().when(traineeService).deleteTrainee(AUTH_USERNAME, AUTH_PASSWORD, "Cihan.Dilsiz");

        mockMvc.perform(delete(BASE_URL + "/Cihan.Dilsiz")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD))
                .andExpect(status().isOk());
    }

    @Test
    void testToggleTraineeActivation_success() throws Exception {
        doNothing().when(traineeService).toggleActivation(AUTH_USERNAME, AUTH_PASSWORD, "Cihan.Dilsiz");

        mockMvc.perform(patch(BASE_URL + "/Cihan.Dilsiz/toggle-activation")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainingHistory_success() throws Exception {
        TrainingDto training = TrainingDto.builder()
                .id(1L)
                .trainee(mockTrainee)
                .build();

        when(traineeService.getTrainingHistory(eq(AUTH_USERNAME), eq(AUTH_PASSWORD), eq("Cihan.Dilsiz"), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz/training-history")
                        .header("authUsername", AUTH_USERNAME)
                        .header("authPassword", AUTH_PASSWORD)
                        .param("from", "2025-07-21")
                        .param("to", "2025-12-31")
                        .param("trainerName", "Ali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
