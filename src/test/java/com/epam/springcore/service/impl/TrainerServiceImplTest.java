package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
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
import com.epam.springcore.service.ITrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TrainerServiceImpl")
class TrainerServiceImplTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private ITrainingService trainingService;
    @Mock private TrainerMapper trainerMapper;
    @Mock private UserServiceImpl userService;
    @InjectMocks private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTrainer() {
        CreateTrainerRequest request = new CreateTrainerRequest("Ali", "Kaya", Specialization.CARDIO);
        User user = new User();
        TrainerDto dto = new TrainerDto();

        when(userService.createUserEntity(any())).thenReturn(user);
        when(trainerRepository.save(any())).thenReturn(new Trainer());
        when(trainerMapper.toTrainerDto(any())).thenReturn(dto);

        TrainerDto result = trainerService.createTrainer(request);
        assertNotNull(result);
    }

    @Test
    void shouldGetTrainerByUsername() {
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findByUserUsername("cihan")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.getTrainerByUsername("admin", "pass", "cihan");
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenTrainerNotFoundByUsername() {
        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.getTrainerByUsername("admin", "pass", "ghost"));
    }

    @Test
    void shouldThrowWhenAuthenticationFails() {
        when(userService.authenticate("admin", "wrong")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> trainerService.getAllTrainers("admin", "wrong"));
    }

    @Test
    void shouldGetAllTrainers() {
        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findAll()).thenReturn(List.of(new Trainer()));
        when(trainerMapper.toTrainerDtoList(any())).thenReturn(List.of(new TrainerDto()));

        List<TrainerDto> result = trainerService.getAllTrainers("admin", "pass");
        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateTrainer() {
        String username = "cihan";
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
        doNothing().when(trainerMapper).updateTrainerRequest(request, trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.updateTrainer("admin", "pass", username, request);
        assertNotNull(result);
    }

    @Test
    void shouldDeleteTrainer() {
        String username = "cihan";
        Trainer trainer = new Trainer();

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deleteTrainer("admin", "pass", username);
        verify(trainerRepository).delete(trainer);
    }

    @Test
    void shouldToggleActivation() {
        when(userService.authenticate("admin", "pass")).thenReturn(true);
        doNothing().when(userService).activateOrDeactivate("cihan");

        trainerService.toggleActivation("admin", "pass", "cihan");
        verify(userService).activateOrDeactivate("cihan");
    }

    @Test
    void shouldGetTrainingHistory() {
        String username = "cihan";
        Trainer trainer = mock(Trainer.class);
        com.epam.springcore.model.Training training = mock(com.epam.springcore.model.Training.class);
        Trainee trainee = mock(Trainee.class);
        User traineeUser = mock(User.class);

        when(training.getDate()).thenReturn(LocalDate.of(2025, 7, 23));
        when(training.getTrainee()).thenReturn(trainee);
        when(trainee.getUser()).thenReturn(traineeUser);
        when(traineeUser.getFirstName()).thenReturn("Cihan");
        when(traineeUser.getLastName()).thenReturn("Dilsiz");

        when(userService.authenticate("admin", "pass")).thenReturn(true);
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

        List<TrainingDto> result = trainerService.getTrainingHistory(
                "admin", "pass", username,
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 31),
                "cihan", "dilsiz"
        );

        assertEquals(1, result.size());
    }

    @Test
    void shouldCreateTrainerEntity() {
        User user = new User();
        user.setId(1L);
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();

        when(trainerRepository.save(any())).thenReturn(trainer);
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.createTrainerEntity(user, Specialization.YOGA);
        assertNotNull(result);
    }

    @Test
    void shouldGetTrainerById() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerById(1L);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenTrainerNotFoundById() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.getTrainerById(99L));
    }
}