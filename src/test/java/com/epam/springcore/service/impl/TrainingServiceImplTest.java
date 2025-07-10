package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
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

public class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        training = new Training("1", "6", "2024-08-01", "Strength", 60);
    }

    @Test
    @DisplayName("Positive Scenario - Create and Save Training")
    void testCreateTraining_Success() {
        doNothing().when(trainingDao).save(training);
        trainingService.createTraining(training);
        verify(trainingDao, times(1)).save(training);
    }

    @Test
    @DisplayName("Negative Scenario - Create Training with Null Type Should Throw")
    void testCreateTraining_NullType_ShouldThrow() {
        Training invalidTraining = new Training("2", "7", "2024-08-02", null, 45);

        assertThrows(IllegalArgumentException.class, () -> {
            trainingService.createTraining(invalidTraining);
        });

        verify(trainingDao, never()).save(any());
    }

    @Test
    @DisplayName("Positive Scenario - Get Training by ID")
    void testGetTrainingById_Success() {
        when(trainingDao.findById("1")).thenReturn(training);
        Training result = trainingService.getTraining("1");

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("Strength");
        verify(trainingDao, times(1)).findById("1");
    }

    @Test
    @DisplayName("Negative Scenario - Null ID Should Throw")
    void testGetTrainingById_Null_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> {
            trainingService.getTraining(null);
        });
        verify(trainingDao, never()).findById(any());
    }

    @Test
    @DisplayName("Negative Scenario - Not Found Training Should Throw")
    void testGetTrainingById_NotFound_ShouldThrow() {
        when(trainingDao.findById("999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainingService.getTraining("999");
        });

        assertThat(exception.getMessage()).isEqualTo("Training not found with id: 999");
        verify(trainingDao, times(1)).findById("999");
    }

    @Test
    @DisplayName("Positive Scenario - Get All Trainings")
    void testGetAllTrainings() {
        List<Training> trainingList = List.of(
                new Training("1", "6", "2024-08-01", "Strength", 60),
                new Training("2", "7", "2024-08-02", "Cardio", 45)
        );

        when(trainingDao.findAll()).thenReturn(trainingList);

        List<Training> result = (List<Training>) trainingService.getAllTrainings();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo("Strength");
        assertThat(result.get(1).getType()).isEqualTo("Cardio");

        verify(trainingDao, times(1)).findAll();
    }
}
