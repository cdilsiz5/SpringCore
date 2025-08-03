package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.TrainingTypeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TrainingMapper;
import com.epam.springcore.mapper.TrainingTypeMapper;
import com.epam.springcore.model.*;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainingRepository;
import com.epam.springcore.repository.TrainingTypeRepository;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
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

class TrainingServiceImplTest {

    private static final String SYSTEM_ADMIN_USERNAME = "system-admin";

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private User trainerUser;
    private User traineeUser;
    private Trainer testTrainer;
    private Trainee testTrainee;
    private Training testTraining;
    private TrainingType testTrainingType;
    private TrainingDto testTrainingDto;
    private TrainingTypeDto testTrainingTypeDto;
    private CreateTrainingRequest createRequest;
    private UpdateTrainingRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "TEST-TX-004");

        trainerUser = User.builder()
                .id(1L)
                .username("jane.trainer")
                .password("password123")
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

        testTrainer = Trainer.builder()
                .id(1L)
                .user(trainerUser)
                .specialization(Specialization.CARDIO)
                .build();

        testTrainee = Trainee.builder()
                .id(1L)
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        testTrainingType = TrainingType.builder()
                .id(1L)
                .name(Specialization.CARDIO)
                .build();

        testTraining = Training.builder()
                .id(1L)
                .traineeId(1L)
                .trainerId(1L)
                .date(LocalDate.now())
                .durationMinutes(60)
                .trainer(testTrainer)
                .trainee(testTrainee)
                .trainingType(testTrainingType)
                .build();

        testTrainingDto = TrainingDto.builder()
                .id(1L)
                .date(LocalDate.now())
                .duration(60)
                .build();

        testTrainingTypeDto = TrainingTypeDto.builder()
                .id(1)
                .specialization(Specialization.CARDIO)
                .build();

        createRequest = CreateTrainingRequest.builder()
                .traineeId(1L)
                .trainerId(1L)
                .type(1L)
                .date(LocalDate.now().toString())
                .durationMinutes(60)
                .build();

        updateRequest = UpdateTrainingRequest.builder()
                .date(LocalDate.now().plusDays(1).toString())
                .durationMinutes(90)
                .build();
    }


    @Test
    void createTraining_ShouldReturnTrainingDto_WhenValidRequest() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);
        when(trainingMapper.toTrainingDto(testTraining)).thenReturn(testTrainingDto);

        TrainingDto result = trainingService.createTraining(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(60, result.getDuration());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void createTraining_ShouldThrowNotFoundException_WhenTrainingTypeNotFound() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingService.createTraining(createRequest)
        );

        assertEquals("Training type not found for id: 1", exception.getMessage());
        verify(trainingRepository, never()).save(any());
    }


    @Test
    void getTraining_ShouldReturnTrainingDto_WhenAuthenticatedAndTrainingExists() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));
        when(trainingMapper.toTrainingDto(testTraining)).thenReturn(testTrainingDto);
        doNothing().when(userService).logout(SYSTEM_ADMIN_USERNAME);

        TrainingDto result = trainingService.getTraining(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).logout(SYSTEM_ADMIN_USERNAME);
    }

    @Test
    void getTraining_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainingService.getTraining(1L)
        );

        assertEquals("SystemAdmin not authenticated", exception.getMessage());
        verify(trainingRepository, never()).findById(any());
    }

    @Test
    void getTraining_ShouldThrowNotFoundException_WhenTrainingNotFound() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingService.getTraining(1L)
        );

        assertEquals("Training with id: 1 not found", exception.getMessage());
    }


    @Test
    void getAllTrainings_ShouldReturnTrainingDtoList_WhenAuthenticatedAndTrainingsExist() {
        List<Training> trainings = List.of(testTraining);
        List<TrainingDto> trainingDtos = List.of(testTrainingDto);

        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findAll()).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(trainingDtos);
        doNothing().when(userService).logout(SYSTEM_ADMIN_USERNAME);

        List<TrainingDto> result = trainingService.getAllTrainings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(userService).logout(SYSTEM_ADMIN_USERNAME);
    }


    @Test
    void getAllTrainings_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainingService.getAllTrainings()
        );

        assertEquals("SystemAdmin not authenticated", exception.getMessage());
        verify(trainingRepository, never()).findAll();
    }


    @Test
    void updateTraining_ShouldReturnUpdatedTrainingDto_WhenValidRequest() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));
        doNothing().when(trainingMapper).updateTrainingRequest(updateRequest, testTraining);
        when(trainingRepository.save(testTraining)).thenReturn(testTraining);
        when(trainingMapper.toTrainingDto(testTraining)).thenReturn(testTrainingDto);
        doNothing().when(userService).logout(SYSTEM_ADMIN_USERNAME);

        TrainingDto result = trainingService.updateTraining(1L, updateRequest);

        assertNotNull(result);
        verify(trainingMapper).updateTrainingRequest(updateRequest, testTraining);
        verify(trainingRepository).save(testTraining);
        verify(userService).logout(SYSTEM_ADMIN_USERNAME);
    }

    @Test
    void updateTraining_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainingService.updateTraining(1L, updateRequest)
        );

        assertEquals("SystemAdmin not authenticated", exception.getMessage());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void deleteTraining_ShouldDeleteTraining_WhenAuthenticatedAndTrainingExists() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));
        doNothing().when(trainingRepository).delete(testTraining);
        doNothing().when(userService).logout(SYSTEM_ADMIN_USERNAME);

        trainingService.deleteTraining(1L);

        verify(trainingRepository).delete(testTraining);
        verify(userService).logout(SYSTEM_ADMIN_USERNAME);
    }

    @Test
    void deleteTraining_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainingService.deleteTraining(1L)
        );

        assertEquals("SystemAdmin not authenticated", exception.getMessage());
        verify(trainingRepository, never()).delete(any());
    }

    @Test
    void deleteTraining_ShouldThrowNotFoundException_WhenTrainingNotFound() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(true);
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingService.deleteTraining(1L)
        );

        assertEquals("Training with id: 1 not found", exception.getMessage());
        verify(trainingRepository, never()).delete(any());
    }

    @Test
    void findAllByTrainer_ShouldReturnTrainingDtoList_WhenTrainingsExist() {
        List<Training> trainings = List.of(testTraining);
        List<TrainingDto> trainingDtos = List.of(testTrainingDto);

        when(trainingRepository.findAllByTrainer(testTrainer)).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(trainingDtos);

        List<TrainingDto> result = trainingService.findAllByTrainer(testTrainer);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(trainingRepository).findAllByTrainer(testTrainer);
    }


    @Test
    void findAllByTrainee_ShouldReturnTrainingDtoList_WhenTrainingsExist() {
        List<Training> trainings = List.of(testTraining);
        List<TrainingDto> trainingDtos = List.of(testTrainingDto);

        when(trainingRepository.findAllByTrainee(testTrainee)).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(trainingDtos);

        List<TrainingDto> result = trainingService.findAllByTrainee(testTrainee);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(trainingRepository).findAllByTrainee(testTrainee);
    }



    @Test
    void findTrainingTypeByName_ShouldReturnTrainingType_WhenExists() {
        when(trainingTypeRepository.findByName(Specialization.CARDIO)).thenReturn(Optional.of(testTrainingType));

        TrainingType result = trainingService.findTrainingTypeByName(Specialization.CARDIO);

        assertNotNull(result);
        assertEquals(Specialization.CARDIO, result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void findTrainingTypeByName_ShouldThrowNotFoundException_WhenNotFound() {
        when(trainingTypeRepository.findByName(Specialization.YOGA)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> trainingService.findTrainingTypeByName(Specialization.YOGA)
        );

        assertEquals("Training type not found for specialization: YOGA", exception.getMessage());
    }


    @Test
    void getAllTrainingTypes_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated(SYSTEM_ADMIN_USERNAME)).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> trainingService.getAllTrainingTypes()
        );

        assertEquals("SystemAdmin not authenticated", exception.getMessage());
        verify(trainingTypeRepository, never()).findAll();
    }


    @Test
    void createTraining_ShouldHandleNegativeDuration() {
        CreateTrainingRequest negativeDurationRequest = CreateTrainingRequest.builder()
                .traineeId(1L)
                .trainerId(1L)
                .type(1L)
                .date(LocalDate.now().toString())
                .durationMinutes(-30)
                .build();

        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);
        when(trainingMapper.toTrainingDto(any(Training.class))).thenReturn(testTrainingDto);

        TrainingDto result = trainingService.createTraining(negativeDurationRequest);

        assertNotNull(result);
        verify(trainingRepository).save(argThat(training ->
                training.getDurationMinutes() != null && training.getDurationMinutes() == -30));
    }

}