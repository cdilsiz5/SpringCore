package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;
import com.epam.springcore.service.ITrainerService;
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
    private ITrainerService trainerService;
    @Mock
    private ITrainingService trainingService;
    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create trainee entity")
    void shouldCreateTraineeEntity() {
        User user = new User();
        LocalDate dob = LocalDate.of(1995, 1, 1);
        String address = "Test Address";

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(dob);
        trainee.setAddress(address);

        TraineeDto expectedDto = new TraineeDto();

        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(expectedDto);

        TraineeDto result = traineeService.createTraineeEntity(user, dob, address);

        assertNotNull(result);
        verify(traineeRepository).save(any());
        verify(traineeMapper).toTraineeDto(trainee);
    }

    @Test
    @DisplayName("Should return trainee by username")
    void shouldReturnTraineeByUsername() {
        String username = "Cihan.Dilsiz";
        Trainee trainee = new Trainee();
        TraineeDto dto = new TraineeDto();

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.getTraineeByUsername(username);

        assertNotNull(result);
        verify(traineeRepository).findByUserUsername(username);
    }

    @Test
    @DisplayName("Should throw when trainee not found")
    void shouldThrowIfTraineeNotFound() {
        when(traineeRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> traineeService.getTraineeByUsername("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("Should update trainee")
    void shouldUpdateTrainee() {
        String username = "Cihan.Dilsiz";
        Trainee trainee = new Trainee();
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        TraineeDto dto = new TraineeDto();

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.updateTrainee(username, request);

        assertNotNull(result);
        verify(traineeRepository).save(trainee);
        verify(traineeMapper).toTraineeDto(trainee);
    }

    @Test
    @DisplayName("Should throw when updating non-existent trainee")
    void shouldThrowWhenUpdatingNonExistentTrainee() {
        when(traineeRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> traineeService.updateTrainee("Cihan.Dilsiz", new UpdateTraineeRequest()));
    }

    @Test
    @DisplayName("Should delete trainee")
    void shouldDeleteTrainee() {
        String username = "Cihan.Dilsiz";
        Trainee trainee = new Trainee();

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(username);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent trainee")
    void shouldThrowWhenDeletingNonExistentTrainee() {
        when(traineeRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("Should update trainer list")
    void shouldUpdateTrainerList() {
        String username = "Cihan.Dilsiz";
        Long trainerId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());
        TraineeDto dto = new TraineeDto();

        UpdateTraineeTrainerListRequest request = new UpdateTraineeTrainerListRequest();
        request.setTrainerIds(List.of(1L));

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerService.getTrainerById(trainerId)).thenReturn(trainer);
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toTraineeDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.updateTrainerList(username, request);

        assertNotNull(result);
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Should return all trainees")
    void shouldReturnAllTrainees() {
        List<Trainee> trainees = List.of(new Trainee());
        List<TraineeDto> dtoList = List.of(new TraineeDto());

        when(traineeRepository.findAll()).thenReturn(trainees);
        when(traineeMapper.toTraineeDtoList(trainees)).thenReturn(dtoList);

        List<TraineeDto> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
        verify(traineeRepository).findAll();
    }

    @Test
    @DisplayName("Should return filtered training history")
    void shouldReturnFilteredTrainingHistory() {
        String username = "Cihan.Dilsiz";
        Trainee trainee = new Trainee();
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setDate(LocalDate.of(2025, 7, 24));
        trainingDto.setTrainer(new TrainerDto());

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingService.findAllByTrainee(trainee)).thenReturn(List.of(trainingDto));

        List<TrainingDto> result = traineeService.getTrainingHistory(username, "2025-07-21", "2025-12-31", null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return unassigned trainers")
    void shouldReturnUnassignedTrainers() {
        String username = "Cihan.Dilsiz";
        Trainee trainee = new Trainee();
        Trainer assignedTrainer = new Trainer();
        TrainerDto trainerDto = new TrainerDto();

        trainee.setTrainers(Set.of(assignedTrainer));

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerService.getAllTrainers()).thenReturn(List.of(trainerDto));

        List<TrainerDto> result = traineeService.getUnassignedTrainers(username);
        assertEquals(1, result.size());
    }
}
