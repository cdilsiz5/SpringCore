package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TrainerMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainerRepository;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.service.ITrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TrainerServiceImpl")
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private ITrainingService trainingService;
    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create trainer entity successfully")
    void shouldCreateTrainerEntity() {
        User user = new User();
        user.setId(1L);
        Specialization spec = Specialization.YOGA;
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();

        when(trainerRepository.save(any())).thenReturn(trainer);
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.createTrainerEntity(user, spec);

        assertNotNull(result);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should return trainer by username")
    void shouldReturnTrainerByUsername() {
        String username = "Cihan.Dilsiz";
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.getTrainerByUsername(username);

        assertNotNull(result);
        verify(trainerRepository).findByUserUsername(username);
    }

    @Test
    @DisplayName("Should throw if trainer not found by username")
    void shouldThrowIfTrainerNotFoundByUsername() {
        when(trainerRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.getTrainerByUsername("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("Should return all trainers")
    void shouldReturnAllTrainers() {
        List<Trainer> trainers = List.of(new Trainer());
        List<TrainerDto> dtoList = List.of(new TrainerDto());

        when(trainerRepository.findAll()).thenReturn(trainers);
        when(trainerMapper.toTrainerDtoList(trainers)).thenReturn(dtoList);

        List<TrainerDto> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should update trainer information")
    void shouldUpdateTrainer() {
        String username = "Cihan.Dilsiz";
        Trainer trainer = new Trainer();
        TrainerDto dto = new TrainerDto();
        UpdateTrainerRequest request = new UpdateTrainerRequest();

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
        doNothing().when(trainerMapper).updateTrainerRequest(request, trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toTrainerDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.updateTrainer(username, request);

        assertNotNull(result);
        verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("Should throw when updating non-existent trainer")
    void shouldThrowWhenUpdatingNonExistentTrainer() {
        when(trainerRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.updateTrainer("Cihan.Dilsiz", new UpdateTrainerRequest()));
    }

    @Test
    @DisplayName("Should delete trainer successfully")
    void shouldDeleteTrainer() {
        String username = "Cihan.Dilsiz";
        Trainer trainer = new Trainer();

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deleteTrainer(username);

        verify(trainerRepository).delete(trainer);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent trainer")
    void shouldThrowWhenDeletingNonExistentTrainer() {
        when(trainerRepository.findByUserUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.deleteTrainer("Cihan.Dilsiz"));
    }

    @Test
    @DisplayName("Should return filtered training history")
    void shouldReturnFilteredTrainingHistory() {
        String username = "john";
        Trainer trainer = new Trainer();
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setDate(LocalDate.of(2025, 7, 21));

        UserDto user = UserDto.builder()
                .id(1L)
                .firstName("Cihan")
                .lastName("Dilsiz")
                .username("cihan.dilsiz")
                .userActive(true)
                .build();

        com.epam.springcore.dto.TraineeDto traineeDto = new com.epam.springcore.dto.TraineeDto();
        traineeDto.setUser(user);

        trainingDto.setTrainee(traineeDto);

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingService.findAllByTrainer(trainer)).thenReturn(List.of(trainingDto));

        List<TrainingDto> result = trainerService.getTrainingHistory(
                username,
                LocalDate.of(2025, 7, 21),
                LocalDate.of(2025, 12, 31),
                null,
                null
        );

        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("Should get trainer by ID")
    void shouldGetTrainerById() {
        Trainer trainer = new Trainer();
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw when trainer not found by ID")
    void shouldThrowWhenTrainerNotFoundById() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.getTrainerById(99L));
    }
}
