package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee mockTrainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockTrainee = new Trainee();
        mockTrainee.setUserId("1");
        mockTrainee.setDateOfBirth(LocalDate.of(1999, 5, 15));
        mockTrainee.setAddress("İstanbul");
    }

    @Test
    @DisplayName("Positive Scenario - Create and Save Trainee")
    void testCreateTrainee_Success() {
        doNothing().when(traineeDao).save(mockTrainee);
        traineeService.createTrainee(mockTrainee);
        verify(traineeDao, times(1)).save(mockTrainee);
    }

    @Test
    @DisplayName("Negative Scenario - Create Trainee with Null Address Should Fail")
    void testCreateTrainee_WithNullAddress_ShouldThrowException() {
        mockTrainee.setAddress(null);

        assertThrows(IllegalArgumentException.class, () -> {
            traineeService.createTrainee(mockTrainee);
        });

        verify(traineeDao, never()).save(any());
    }

    @Test
    @DisplayName("Positive Scenario - Get Trainee By Id")
    void testGetTraineeById_Success() {
        when(traineeDao.findById("1")).thenReturn(mockTrainee);

        Trainee found = traineeService.getTrainee("1");

        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo("1");

        verify(traineeDao, times(1)).findById("1");
    }

    @Test
    @DisplayName("Negative Scenario - Get Trainee with Null Id Should Throw")
    void testGetTraineeById_NullId_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> {
            traineeService.getTrainee(null);
        });

        verify(traineeDao, never()).findById(any());
    }

    @Test
    @DisplayName("Negative Scenario - Get Trainee with Non-existing Id Should Throw")
    void testGetTraineeById_NotFound_ShouldThrow() {
        when(traineeDao.findById("404")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            traineeService.getTrainee("404");
        });

        assertThat(exception.getMessage()).isEqualTo("Trainee not found with id: 404");
        verify(traineeDao, times(1)).findById("404");
    }

    @Test
    @DisplayName("Positive Scenario - Get All Trainees")
    void testGetAllTrainees() {
        List<Trainee> traineeList = List.of(mockTrainee, mockTrainee);
        when(traineeDao.findAll()).thenReturn(traineeList);

        List<Trainee> result = (List<Trainee>) traineeService.getAllTrainees();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(traineeDao, times(1)).findAll();
    }
}
