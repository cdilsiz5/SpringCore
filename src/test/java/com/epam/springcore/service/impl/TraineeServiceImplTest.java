package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.*;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainer.TrainerUsernameRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITrainerService;
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
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private ITrainingService trainingService;

    @Mock
    private ITrainerService trainerService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private User testUser;
    private User trainerUser;
    private Trainee testTrainee;
    private Trainer testTrainer;
    private TraineeDto testTraineeDto;
    private TrainerDto testTrainerDto;
    private TrainingDto testTrainingDto;
    private CreateTraineeRequest createRequest;
    private UpdateTraineeRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "TEST-TX-002");

        testUser = User.builder()
                .id(1L)
                .username("john.trainee")
                .password("password123")
                .firstName("John")
                .lastName("Trainee")
                .userActive(true)
                .build();

        trainerUser = User.builder()
                .id(2L)
                .username("jane.trainer")
                .password("password456")
                .firstName("Jane")
                .lastName("Trainer")
                .userActive(true)
                .build();

        testTrainee = Trainee.builder()
                .id(1L)
                .user(testUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        testTrainer = Trainer.builder()
                .id(1L)
                .user(trainerUser)
                .specialization(Specialization.BOXING)
                .build();

        testTraineeDto = TraineeDto.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        testTrainerDto = TrainerDto.builder()
                .id(1L)
                .specialization(Specialization.BOXING.name())
                .build();

        testTrainingDto = TrainingDto.builder()
                .id(1L)
                .date(LocalDate.now())
                .duration(60)
                .build();

        createRequest = CreateTraineeRequest.builder()
                .firstName("John")
                .lastName("Trainee")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        updateRequest = UpdateTraineeRequest.builder()
                .dateOfBirth(LocalDate.of(1991, 1, 1))
                .address("456 New St")
                .build();
    }


    @Test
    void createTrainee_ShouldReturnLoginCredentials_WhenValidRequest() {
        User createdUser = User.builder()
                .username("generated.username")
                .password("generatedPassword")
                .firstName("John")
                .lastName("Trainee")
                .userActive(false)
                .build();

        when(userService.createUserEntity(any(CreateUserRequest.class))).thenReturn(createdUser);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);
        when(traineeMapper.toTraineeDto(any(Trainee.class))).thenReturn(testTraineeDto);

        LoginCredentialsResponse result = traineeService.createTrainee(createRequest);

        assertNotNull(result);
        assertEquals("generated.username", result.getUsername());
        assertEquals("generatedPassword", result.getPassword());
        verify(userService).createUserEntity(any(CreateUserRequest.class));
        verify(traineeRepository).save(any(Trainee.class));
    }


    @Test
    void getTraineeByUsername_ShouldReturnTraineeDto_WhenAuthenticatedAndTraineeExists() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        when(traineeMapper.toTraineeDto(testTrainee)).thenReturn(testTraineeDto);
        doNothing().when(userService).logout("john.trainee");

        TraineeDto result = traineeService.getTraineeByUsername("john.trainee");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getAddress());
        verify(userService).logout("john.trainee");
    }

    @Test
    void getTraineeByUsername_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> traineeService.getTraineeByUsername("john.trainee")
        );

        assertEquals("User not authenticated: john.trainee", exception.getMessage());
        verify(traineeRepository, never()).findByUserUsername(any());
        verify(userService, never()).logout(any());
    }

    @Test
    void getTraineeByUsername_ShouldThrowNotFoundException_WhenTraineeNotFound() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> traineeService.getTraineeByUsername("john.trainee")
        );

        assertEquals("Trainee not found: john.trainee", exception.getMessage());
    }


    @Test
    void getAllTrainees_ShouldReturnTraineeDtoList_WhenTraineesExist() {
        List<Trainee> trainees = List.of(testTrainee);
        List<TraineeDto> traineeDtos = List.of(testTraineeDto);

        when(traineeRepository.findAll()).thenReturn(trainees);
        when(traineeMapper.toTraineeDtoList(trainees)).thenReturn(traineeDtos);

        List<TraineeDto> result = traineeService.getAllTrainees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }


    @Test
    void updateTrainee_ShouldReturnUpdatedTraineeDto_WhenValidRequest() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeMapper).updateTraineeRequest(updateRequest, testTrainee);
        when(traineeRepository.save(testTrainee)).thenReturn(testTrainee);
        when(traineeMapper.toTraineeDto(testTrainee)).thenReturn(testTraineeDto);
        doNothing().when(userService).logout("john.trainee");

        TraineeDto result = traineeService.updateTrainee("john.trainee", updateRequest);

        assertNotNull(result);
        verify(traineeMapper).updateTraineeRequest(updateRequest, testTrainee);
        verify(traineeRepository).save(testTrainee);
        verify(userService).logout("john.trainee");
    }


    @Test
    void deleteTrainee_ShouldDeleteTrainee_WhenAuthenticatedAndTraineeExists() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeRepository).delete(testTrainee);
        doNothing().when(userService).logout("john.trainee");

        traineeService.deleteTrainee("john.trainee");

        verify(traineeRepository).delete(testTrainee);
        verify(userService).logout("john.trainee");
    }

    @Test
    void deleteTrainee_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> traineeService.deleteTrainee("john.trainee")
        );

        assertEquals("User not authenticated: john.trainee", exception.getMessage());
        verify(traineeRepository, never()).delete(any());
    }


    @Test
    void toggleActivation_ShouldToggleActivation_WhenAuthenticated() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        doNothing().when(userService).activateOrDeactivate("john.trainee");
        doNothing().when(userService).logout("john.trainee");

        traineeService.toggleActivation("john.trainee");

        verify(userService).activateOrDeactivate("john.trainee");
        verify(userService).logout("john.trainee");
    }

    @Test
    void toggleActivation_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> traineeService.toggleActivation("john.trainee")
        );

        assertEquals("User not authenticated: john.trainee", exception.getMessage());
        verify(userService, never()).activateOrDeactivate(any());
    }




    @Test
    void getTrainingHistory_ShouldReturnAllTrainings_WhenNoFiltersProvided() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        when(trainingService.findAllByTrainee(testTrainee)).thenReturn(List.of(testTrainingDto));
        doNothing().when(userService).logout("john.trainee");

        List<TrainingDto> result = traineeService.getTrainingHistory(
                "john.trainee", null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).findAllByTrainee(testTrainee);
    }

    @Test
    void getTrainingHistory_ShouldFilterByDateRange_WhenDatesProvided() {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 12, 31);

        TrainingDto pastTraining = TrainingDto.builder()
                .id(1L)
                .date(LocalDate.of(2024, 6, 15))
                .trainer(testTrainerDto)
                .build();

        TrainingDto futureTraining = TrainingDto.builder()
                .id(2L)
                .date(LocalDate.of(2025, 6, 15))
                .trainer(testTrainerDto)
                .build();

        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        when(trainingService.findAllByTrainee(testTrainee)).thenReturn(List.of(pastTraining, futureTraining));
        doNothing().when(userService).logout("john.trainee");

        List<TrainingDto> result = traineeService.getTrainingHistory(
                "john.trainee", fromDate, toDate, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }


    @Test
    void getTraineeById_ShouldReturnTrainee_WhenTraineeExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));

        Trainee result = traineeService.getTraineeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getAddress());
    }

    @Test
    void getTraineeById_ShouldThrowNotFoundException_WhenTraineeNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> traineeService.getTraineeById(1L)
        );

        assertEquals("Trainee with id 1 not found", exception.getMessage());
    }

    @Test
    void updateTrainerList_ShouldAssignTrainers_WhenValidRequest() {
        TrainerUsernameRequest trainerRequest = TrainerUsernameRequest.builder()
                .username("jane.trainer")
                .build();

        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .name(Specialization.BOXING)
                .build();

        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        when(trainingService.findAllByTrainee(testTrainee)).thenReturn(List.of());
        when(trainerService.getTrainerByUsername("jane.trainer")).thenReturn(testTrainerDto);
        when(trainerService.getTrainerById(1L)).thenReturn(testTrainer);
        when(trainingService.findTrainingTypeByName(Specialization.BOXING)).thenReturn(trainingType);
        when(trainingService.createTraining(any())).thenReturn(testTrainingDto);
        doNothing().when(userService).logout("john.trainee");

        List<TrainerDto> result = traineeService.updateTrainerList("john.trainee", List.of(trainerRequest));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).createTraining(any());
        verify(userService).logout("john.trainee");
    }

    @Test
    void updateTrainerList_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        TrainerUsernameRequest trainerRequest = TrainerUsernameRequest.builder()
                .username("jane.trainer")
                .build();

        when(userService.isAuthenticated("john.trainee")).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> traineeService.updateTrainerList("john.trainee", List.of(trainerRequest))
        );

        assertEquals("User not authenticated: john.trainee", exception.getMessage());
        verify(trainingService, never()).createTraining(any());
    }

    @Test
    void updateTrainerList_ShouldHandleEmptyTrainerList_Gracefully() {
        when(userService.isAuthenticated("john.trainee")).thenReturn(true);
        when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(testTrainee));
        when(trainingService.findAllByTrainee(testTrainee)).thenReturn(List.of());
        doNothing().when(userService).logout("john.trainee");

        List<TrainerDto> result = traineeService.updateTrainerList("john.trainee", List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService).logout("john.trainee");
    }



}