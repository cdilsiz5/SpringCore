// This class includes full unit tests for UserController with success and failure cases
// covering: getUser, getAllUsers, deleteUser, updatePassword, login, createTrainee, createTrainer, toggleActivation

package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.handler.GlobalExceptionHandler;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IUserService userService;

    private final String BASE_URL = "/api/epam/v1/users";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());}

    @Test
    @DisplayName("GET /users/{username} - Success")
    void testGetUser_success() throws Exception {
        UserDto user = UserDto.builder().username("Cihan.Dilsiz").firstName("Cihan").lastName("Dilsiz").isActive(true).build();
        when(userService.getUserByUsername("Cihan.Dilsiz")).thenReturn(user);

        mockMvc.perform(get(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("GET /users/{username} - Not Found")
    void testGetUser_notFound() throws Exception {
        when(userService.getUserByUsername("notfound"))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL + "/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("GET /users - Success")
    void testGetAllUsers_success() throws Exception {
        UserDto user = UserDto.builder().username("Cihan.Dilsiz").build();
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("DELETE /users/{username} - Success")
    void testDeleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser("Cihan.Dilsiz");

        mockMvc.perform(delete(BASE_URL + "/Cihan.Dilsiz"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /users/{username}/password - Success")
    void testUpdatePassword_success() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPass", "newPass");
        UserDto user = UserDto.builder().username("Cihan.Dilsiz").build();

        when(userService.updatePassword(eq("Cihan.Dilsiz"), any(UpdatePasswordRequest.class)))
                .thenReturn(user);

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("PUT /users/{username}/password - Invalid Old Password")
    void testUpdatePassword_invalidOldPassword() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrong", "newPass");
        when(userService.updatePassword(eq("Cihan.Dilsiz"), any(UpdatePasswordRequest.class)))
                .thenThrow(new InvalidCredentialsException("Old password does not match."));

        mockMvc.perform(put(BASE_URL + "/Cihan.Dilsiz/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Old password does not match."));
    }

    @Test
    @DisplayName("POST /users/login - Success")
    void testLogin_success() throws Exception {
        LoginRequest request = new LoginRequest("Cihan.Dilsiz", "pass");
        when(userService.login(any(LoginRequest.class))).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("POST /users/login - Invalid Credentials")
    void testLogin_invalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("Cihan.Dilsiz", "wrong");
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /users/trainee - Create Trainee")
    void testCreateTrainee_success() throws Exception {
        CreateTraineeRequest request = new CreateTraineeRequest("Cihan", "Dilsiz", LocalDate.of(2000,01,01), "Ankara");
        TraineeDto trainee = TraineeDto.builder()
                .address("Ankara")
                .dateOfBirth(LocalDate.of(2000,01,01))
                .build();
        when(userService.createTrainee(any(CreateTraineeRequest.class))).thenReturn(trainee);

        mockMvc.perform(post(BASE_URL + "/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("Ankara"));
    }

    @Test
    @DisplayName("POST /users/trainer - Create Trainer")
    void testCreateTrainer_success() throws Exception {
        CreateTrainerRequest request = new CreateTrainerRequest("Ali", "Yılmaz", Specialization.CARDIO);
        TrainerDto trainer = TrainerDto.builder().specialization("CARDIO").build();
        when(userService.createTrainer(any(CreateTrainerRequest.class))).thenReturn(trainer);

        mockMvc.perform(post(BASE_URL + "/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialization").value("CARDIO"));
    }

    @Test
    @DisplayName("PATCH /users/{username}/toggle-activation - Success")
    void testToggleActivation_success() throws Exception {
        doNothing().when(userService).activateOrDeactivate("Cihan.Dilsiz");

        mockMvc.perform(patch(BASE_URL + "/Cihan.Dilsiz/toggle-activation"))
                .andExpect(status().isOk());
    }
} 
