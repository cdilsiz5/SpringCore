package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.service.ITrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TraineeServiceImpl")
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private ITrainingService trainingService;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTrainee() {
        CreateTraineeRequest request = new CreateTraineeRequest("Cihan", "Dilsiz",LocalDate.of(1999,07,19),"Mersin");
        User user = new User();
        TraineeDto dto = new TraineeDto();

        when(userService.createUserEntity(any(CreateUserRequest.class))).thenReturn(user);
        when(traineeRepository.save(any())).thenReturn(new Trainee());
        when(traineeMapper.toTraineeDto(any())).thenReturn(dto);

        TraineeDto result = traineeService.createTrainee(request);
        assertNotNull(result);
    }

    @Test
    void shouldGetTraineeByUsername() {
        User user = new User();
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        TraineeDto dto = new TraineeDto();

        when(userService.authenticate("admin", "pass"))
                .thenReturn(true);
        when(traineeRepository.findByUserUsername("cihan"))
                .thenReturn(Optional.of(trainee));
        when(traineeMapper.toTraineeDto(trainee))
                .thenReturn(dto);

        TraineeDto result = traineeService.getTraineeByUsername("admin", "pass", "cihan");
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenTraineeNotFoundByUsername() {
        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(traineeRepository.findByUserUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> traineeService.getTraineeByUsername("admin", "pass", "ghost"));
    }

    @Test
    void shouldThrowWhenAuthenticationFails() {
        when(userService.authenticate("admin", "wrong"))
                .thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> traineeService.getAllTrainees("admin", "wrong"));
    }

    @Test
    void shouldGetAllTrainees() {
        when(userService.authenticate("admin", "pass"))
                .thenReturn(true);
        when(traineeRepository.findAll())
                .thenReturn(List.of(new Trainee()));
        when(traineeMapper.toTraineeDtoList(any()))
                .thenReturn(List.of(new TraineeDto()));

        List<TraineeDto> result = traineeService.getAllTrainees("admin", "pass");
        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateTrainee() {
        String username = "cihan";
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        Trainee trainee = new Trainee();
        TraineeDto dto = new TraineeDto();

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        doNothing().when(traineeMapper).updateTraineeRequest(request, trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.updateTrainee("admin", "pass", username, request);
        assertNotNull(result);
    }

    @Test
    void shouldDeleteTrainee() {
        String username = "cihan";
        Trainee trainee = new Trainee();

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("admin", "pass", username);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void shouldToggleActivation() {
        when(userService.authenticate("admin", "pass")).thenReturn(true);
        doNothing().when(userService).activateOrDeactivate("cihan");

        traineeService.toggleActivation("admin", "pass", "cihan");
        verify(userService).activateOrDeactivate("cihan");
    }

    @Test
    void shouldGetTrainingHistory() {
        String username = "cihan";
        Trainee trainee = mock(Trainee.class);
        TrainingDto dto = new TrainingDto();

        // training mock structure
        com.epam.springcore.model.Training training = mock(com.epam.springcore.model.Training.class);
        Trainer trainer = mock(Trainer.class);
        User trainerUser = mock(User.class);
        Trainee linkedTrainee = mock(Trainee.class);
        User traineeUser = mock(User.class);

        when(training.getDate()).thenReturn(LocalDate.of(2025, 7, 23));
        when(training.getTrainer()).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(trainerUser);
        when(trainerUser.getFirstName()).thenReturn("Ahmet");
        when(trainerUser.getLastName()).thenReturn("Yılmaz");
        when(training.getTrainee()).thenReturn(linkedTrainee);
        when(linkedTrainee.getUser()).thenReturn(traineeUser);
        when(traineeUser.getFirstName()).thenReturn("Cihan");
        when(traineeUser.getLastName()).thenReturn("Dilsiz");

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

        List<TrainingDto> result = traineeService.getTrainingHistory(
                "admin", "pass", username,
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 31),
                "ahmet", "yılmaz"
        );

        assertEquals(1, result.size());
    }

    @Test
    void shouldCreateTraineeEntity() {
        User user = new User();
        user.setId(1L);
        Trainee trainee = new Trainee();
        TraineeDto dto = new TraineeDto();

        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.createTraineeEntity(user);
        assertNotNull(result);
    }

    @Test
    void shouldGetTraineeById() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeById(1L);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenTraineeNotFoundById() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.getTraineeById(99L));
    }
}
