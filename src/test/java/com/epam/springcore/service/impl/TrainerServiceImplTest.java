package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.enums.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = new Trainer(TrainingType.CROSSFIT, "10");
    }

    @Test
    @DisplayName("Positive Scenario - Create and Save Trainer")
    void testCreateTrainer_Success() {
        doNothing().when(trainerDao).save(trainer);
        trainerService.createTrainer(trainer);
        verify(trainerDao, times(1)).save(trainer);
    }

    @Test
    @DisplayName("Negative Scenario - Create Trainer with Null Specialization")
    void testCreateTrainer_NullSpecialization_ShouldThrow() {
        Trainer invalidTrainer = new Trainer(null, "11");

        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainer(invalidTrainer);
        });

        verify(trainerDao, never()).save(any());
    }

    @Test
    @DisplayName("Negative Scenario - Create Trainer with Null UserId")
    void testCreateTrainer_NullUserId_ShouldThrow() {
        Trainer invalidTrainer = new Trainer(TrainingType.YOGA, null);

        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainer(invalidTrainer);
        });

        verify(trainerDao, never()).save(any());
    }

    @Test
    @DisplayName("Positive Scenario - Get Trainer by ID")
    void testGetTrainerById_Success() {
        when(trainerDao.findById("10")).thenReturn(trainer);

        Trainer result = trainerService.getTrainer("10");

        assertThat(result).isNotNull();
        assertThat(result.getSpecialization()).isEqualTo(TrainingType.CROSSFIT);
        verify(trainerDao, times(1)).findById("10");
    }

    @Test
    @DisplayName("Negative Scenario - Null ID Should Throw")
    void testGetTrainerById_Null_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.getTrainer(null);
        });
        verify(trainerDao, never()).findById(any());
    }

    @Test
    @DisplayName("Negative Scenario - Not Found Trainer Should Throw")
    void testGetTrainerById_NotFound_ShouldThrow() {
        when(trainerDao.findById("99")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainerService.getTrainer("99");
        });

        assertThat(exception.getMessage()).isEqualTo("Trainer not found with id: 99");
        verify(trainerDao, times(1)).findById("99");
    }

    @Test
    @DisplayName("Positive Scenario - Get All Trainers")
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(
                new Trainer(TrainingType.YOGA, "11"),
                new Trainer(TrainingType.PILATES, "12")
        );

        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = (List<Trainer>) trainerService.getAllTrainers();

        assertThat(result).hasSize(2);
        assertThat(result).containsAll(trainers);
        verify(trainerDao, times(1)).findAll();
    }
}
