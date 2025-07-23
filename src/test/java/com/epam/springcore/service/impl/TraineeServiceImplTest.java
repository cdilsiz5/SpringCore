package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.UnauthorizedException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.user.CreateUserRequest;
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
    private TraineeServiceImpl trainingService;
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
    void shouldCreateTrainee() {
        // GIVEN (hazırlık)
        User user = User.builder()
                .username("Cihan")
                .lastName("Dilsiz")
                .password("pass")
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Mersin")
                .build();

        CreateTraineeRequest request = new CreateTraineeRequest();
        request.setFirstName("Cihan");
        request.setLastName("Dilsiz");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("Mersin");

        TraineeDto expectedDto = new TraineeDto();

        // WHEN
        when(traineeMapper.createTrainee(request)).thenReturn(trainee);
        when(userService.createUserEntity(any())).thenReturn(user);
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(expectedDto);

        // THEN
        TraineeDto result = traineeService.createTrainee(request);

        assertNotNull(result);
        assertEquals(expectedDto, result);
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
