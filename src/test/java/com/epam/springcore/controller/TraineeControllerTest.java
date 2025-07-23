package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public  class TraineeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TraineeDto mockTrainee;
    private UserDto mockUser;

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
    @DisplayName("GET /trainees/{username} - Success")
    void testGetTraineeByUsername_success() throws Exception {
        when(traineeService.getTraineeByUsername("Cihan.Dilsiz")).thenReturn(mockTrainee);

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Cihan.Dilsiz"))
                .andExpect(jsonPath("$.address").value("İstanbul"));
    }

    @Test
    @DisplayName("GET /trainees/{username} - Not Found")
    void testGetTraineeByUsername_notFound() throws Exception {
        when(traineeService.getTraineeByUsername("notfound"))
                .thenThrow(new NotFoundException("Trainee not found"));

        mockMvc.perform(get(BASE_URL + "/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("GET /trainees - Get all trainees")
    void testGetAllTrainees_success() throws Exception {
        when(traineeService.getAllTrainees()).thenReturn(List.of(mockTrainee));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /trainees/{username} - Update trainee")
    void testUpdateTrainee_success() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest( LocalDate.of(1998, 5, 5),"Ankara");
        mockTrainee.setAddress("Ankara");

        when(traineeService.updateTrainee(eq("Cihan.Dilsiz"), any(UpdateTraineeRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Ankara"));
    }

    @Test
    @DisplayName("DELETE /trainees/{username} - Success")
    void testDeleteTrainee_success() throws Exception {
        doNothing().when(traineeService).deleteTrainee("Cihan.Dilsiz");

        mockMvc.perform(delete(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /trainees/{username} - Not Found")
    void testDeleteTrainee_notFound() throws Exception {
        doThrow(new NotFoundException("Trainee not found"))
                .when(traineeService).deleteTrainee("Cihan.Dilsiz");

        mockMvc.perform(delete(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"))
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    @DisplayName("PUT /trainees/{username}/trainers - Update trainer list")
    void testUpdateTrainerList_success() throws Exception {
        UpdateTraineeTrainerListRequest request = new UpdateTraineeTrainerListRequest();
        request.setTrainerIds(List.of(1L, 2L));

        when(traineeService.updateTrainerList(eq("Cihan.Dilsiz"), any(UpdateTraineeTrainerListRequest.class)))
                .thenReturn(mockTrainee);

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("GET /trainees/{username}/trainings - Filtered history")
    void testGetTrainingHistory_success() throws Exception {
        TrainingDto training = TrainingDto.builder()
                .id(1L)
                .trainee(mockTrainee)
                .build();

        when(traineeService.getTrainingHistory(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz/trainings")
                        .param("from", "2025-07-21")
                        .param("to", "2025-12-31")
                        .param("trainerName", "Ali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("GET /trainees/{username}/unassigned-trainers - Success")
    void testGetUnassignedTrainers_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("Ali")
                .lastName("Yılmaz")
                .username("ali.yilmaz")
                .userActive(true)
                .build();

        TrainerDto trainer = TrainerDto.builder()
                .specialization("CARDIO")
                .user(userDto)
                .build();

        when(traineeService.getUnassignedTrainers("Cihan.Dilsiz"))
                .thenReturn(List.of(trainer));

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz/unassigned-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].user.firstName").value("Ali"));
    }
}
