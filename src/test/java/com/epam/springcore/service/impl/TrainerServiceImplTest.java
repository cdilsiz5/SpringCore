package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TrainerMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainerRepository;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITrainingService;
import com.epam.springcore.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private ITrainingService trainingService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUser;
    private User traineeUser;
    private UserDto testUserDto;
    private UserDto traineeUserDto;
    private Trainer testTrainer;
    private Trainee testTrainee;
    private TrainerDto testTrainerDto;
    private TraineeDto testTraineeDto;
    private TrainingDto testTrainingDto;
    private CreateTrainerRequest createRequest;
    private UpdateTrainerRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "TEST-TX-003");

        testUser = User.builder()
                .id(1L)
                .username("jane.trainer")
                .password("password123")
                .firstName("Jane")
                .lastName("Trainer")
                .userActive(true)
                .build();

        testUserDto = UserDto.builder()
                .id(1L)
                .username("jane.trainer")
                .firstName("Jane")
                .lastName("Trainer")
                .userActive(true)
                .build();

        traineeUser = User.builder()
                .id(2L)
                .username("john.trainee")
                .password("password456")
                .firstName("John")
                .lastName("Trainee")
                .userActive(true)
                .build();
        traineeUserDto = UserDto.builder()
                .id(2L)
                .username("john.trainee")
                .firstName("John")
                .lastName("Trainee")
                .userActive(true)
                .build();

        testTrainer = Trainer.builder()
                .id(1L)
                .user(testUser)
                .specialization(Specialization.BOXING)
                .build();

        testTrainee = Trainee.builder()
                .id(1L)
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        testTrainerDto = TrainerDto.builder()
                .id(1L)
                .specialization(Specialization.BOXING.name())
                .user(testUserDto)
                .build();

        testTraineeDto = TraineeDto.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .user(traineeUserDto)
                .build();

        testTrainingDto = TrainingDto.builder()
                .id(1L)
                .date(LocalDate.now())
                .duration(60)
                .trainee(testTraineeDto)
                .trainer(testTrainerDto)
                .build();

        createRequest = CreateTrainerRequest.builder()
                .firstName("Jane")
                .lastName("Trainer")
                .specialty(Specialization.BOXING)
                .build();

        updateRequest = UpdateTrainerRequest.builder()
                .specialization(Specialization.YOGA)
                .build();
    }


    @Test
    void createTrainer_ShouldReturnLoginCredentials_WhenValidRequest() {
        User createdUser = User.builder()
                .username("generated.username")
                .password("generatedPassword")
                .firstName("Jane")
                .lastName("Trainer")
                .userActive(false)
                .build();

        when(userService.createUserEntity(any(CreateUserRequest.class))).thenReturn(createdUser);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);
        when(trainerMapper.toTrainerDto(any(Trainer.class))).thenReturn(testTrainerDto);

        LoginCredentialsResponse result = trainerService.createTrainer(createRequest);

        assertNotNull(result);
        assertEquals("generated.username", result.getUsername());
        assertEquals("generatedPassword", result.getPassword());
        verify(userService).createUserEntity(any(CreateUserRequest.class));
        verify(trainerRepository).save(any(Trainer.class));
    }


    @Test
    void getTrainerByUsername_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainerService.getTrainerByUsername("jane.trainer")
        );

        assertEquals("User not authenticated: jane.trainer", exception.getMessage());
        verify(trainerRepository, never()).findByUserUsername(any());
        verify(userService, never()).logout(any());
    }

    @Test
    void getTrainerByUsername_ShouldThrowNotFoundException_WhenTrainerNotFound() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainerService.getTrainerByUsername("jane.trainer")
        );

        assertEquals("Trainer not found: jane.trainer", exception.getMessage());
    }


    @Test
    void getAllTrainers_ShouldReturnTrainerDtoList_WhenTrainersExist() {
        List<Trainer> trainers = List.of(testTrainer);
        List<TrainerDto> trainerDtos = List.of(testTrainerDto);

        when(trainerRepository.findAll()).thenReturn(trainers);
        when(trainerMapper.toTrainerDtoList(trainers)).thenReturn(trainerDtos);

        List<TrainerDto> result = trainerService.getAllTrainers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }


    @Test
    void updateTrainer_ShouldReturnUpdatedTrainerDto_WhenValidRequest() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.of(testTrainer));
        doNothing().when(trainerMapper).updateTrainerRequest(updateRequest, testTrainer);
        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);
        when(trainerMapper.toTrainerDto(testTrainer)).thenReturn(testTrainerDto);
        doNothing().when(userService).logout("jane.trainer");

        TrainerDto result = trainerService.updateTrainer("jane.trainer", updateRequest);

        assertNotNull(result);
        verify(trainerMapper).updateTrainerRequest(updateRequest, testTrainer);
        verify(trainerRepository).save(testTrainer);
        verify(userService).logout("jane.trainer");
    }

    @Test
    void updateTrainer_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainerService.updateTrainer("jane.trainer", updateRequest)
        );

        assertEquals("User not authenticated: jane.trainer", exception.getMessage());
        verify(trainerRepository, never()).save(any());
    }


    @Test
    void deleteTrainer_ShouldDeleteTrainer_WhenAuthenticatedAndTrainerExists() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.of(testTrainer));
        doNothing().when(trainerRepository).delete(testTrainer);
        doNothing().when(userService).logout("jane.trainer");

        trainerService.deleteTrainer("jane.trainer");

        verify(trainerRepository).delete(testTrainer);
        verify(userService).logout("jane.trainer");
    }

    @Test
    void deleteTrainer_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainerService.deleteTrainer("jane.trainer")
        );

        assertEquals("User not authenticated: jane.trainer", exception.getMessage());
        verify(trainerRepository, never()).delete(any());
    }

    @Test
    void deleteTrainer_ShouldThrowNotFoundException_WhenTrainerNotFound() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainerService.deleteTrainer("jane.trainer")
        );

        assertEquals("Trainer not found: jane.trainer", exception.getMessage());
        verify(trainerRepository, never()).delete(any());
    }


    @Test
    void toggleActivation_ShouldToggleActivation_WhenAuthenticated() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        doNothing().when(userService).activateOrDeactivate("jane.trainer");
        doNothing().when(userService).logout("jane.trainer");

        trainerService.toggleActivation("jane.trainer");

        verify(userService).activateOrDeactivate("jane.trainer");
        verify(userService).logout("jane.trainer");
    }


    @Test
    void getTrainingHistory_ShouldReturnAllTrainings_WhenNoFiltersProvided() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.of(testTrainer));
        when(trainingService.findAllByTrainer(testTrainer)).thenReturn(List.of(testTrainingDto));
        doNothing().when(userService).logout("jane.trainer");

        List<TrainingDto> result = trainerService.getTrainingHistory(
                "jane.trainer", null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(trainingService).findAllByTrainer(testTrainer);
        verify(userService).logout("jane.trainer");
    }


    @Test
    void getTrainingHistory_ShouldReturnEmptyList_WhenNoTrainingsMatchFilters() {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 12, 31);

        TrainingDto oldTraining = TrainingDto.builder()
                .id(1L)
                .date(LocalDate.of(2024, 6, 15))
                .trainee(testTraineeDto)
                .build();

        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.of(testTrainer));
        when(trainingService.findAllByTrainer(testTrainer)).thenReturn(List.of(oldTraining));
        doNothing().when(userService).logout("jane.trainer");

        List<TrainingDto> result = trainerService.getTrainingHistory(
                "jane.trainer", fromDate, toDate, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainingHistory_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainerService.getTrainingHistory("jane.trainer", null, null, null, null)
        );

        assertEquals("User not authenticated: jane.trainer", exception.getMessage());
        verify(trainingService, never()).findAllByTrainer(any());
        verify(userService, never()).logout(any());
    }

    @Test
    void getTrainingHistory_ShouldThrowNotFoundException_WhenTrainerNotFound() {
        when(userService.isAuthenticated("jane.trainer")).thenReturn(true);
        when(trainerRepository.findByUserUsername("jane.trainer")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainerService.getTrainingHistory("jane.trainer", null, null, null, null)
        );

        assertEquals("Trainer not found: jane.trainer", exception.getMessage());
        verify(trainingService, never()).findAllByTrainer(any());
        verify(userService, never()).logout(any());
    }


    @Test
    void getTrainerById_ShouldReturnTrainer_WhenTrainerExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        Trainer result = trainerService.getTrainerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Specialization.BOXING, result.getSpecialization());
    }

    @Test
    void getTrainerById_ShouldThrowNotFoundException_WhenTrainerNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainerService.getTrainerById(1L)
        );

        assertEquals("Trainer with id 1 not found", exception.getMessage());
    }

    @Test
    void getTrainerById_ShouldHandleNullId() {
        when(trainerRepository.findById(null)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainerService.getTrainerById(null)
        );

        assertEquals("Trainer with id null not found", exception.getMessage());
    }

}