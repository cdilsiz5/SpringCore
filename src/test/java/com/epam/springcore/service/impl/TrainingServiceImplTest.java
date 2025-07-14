package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.GymNotFoundException;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.create.CreateTrainingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TrainingServiceImpl")
public class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create training and call save method")
    void shouldCallCreateTraining() {
        CreateTrainingRequest request = new CreateTrainingRequest("trainee-1", "trainer-1", "2025-01-01", "CROSSFIT", 60);
        Training training = new Training(
                request.getTraineeId(),
                request.getTrainerId(),
                request.getDate(),
                TrainingType.CROSSFIT,
                request.getDurationMinutes()
        );
        training.setId("training-123");

        User trainee = new User("ali", "trainee", Collections.emptyList());
        trainee.setId("trainee-1");

        User trainer = new User("veli", "trainer", Collections.emptyList());
        trainer.setId("trainer-1");

        when(trainingDao.save(any(Training.class))).thenReturn(training);
        when(userDao.findById("trainee-1")).thenReturn(trainee);
        when(userDao.findById("trainer-1")).thenReturn(trainer);

        TrainingDto result = trainingService.createTraining(request);

        assertNotNull(result);
        assertEquals("trainee-1", result.getTraineeId());
        assertEquals("trainer-1", result.getTrainerId());
        assertEquals("CROSSFIT", result.getType());

        verify(trainingDao).save(any(Training.class));
    }

    @Test
    @DisplayName("Should return training DTO by ID")
    void shouldReturnTrainingById() {
        Training training = new Training("trainee-1", "trainer-1", "2025-01-01", TrainingType.BOXING, 45);
        training.setId("10");

        User trainee = new User("ali", "trainee", Collections.emptyList());
        trainee.setId("trainee-1");

        User trainer = new User("veli", "trainer", Collections.emptyList());
        trainer.setId("trainer-1");

        when(trainingDao.findById("10")).thenReturn(training);
        when(userDao.findById("trainee-1")).thenReturn(trainee);
        when(userDao.findById("trainer-1")).thenReturn(trainer);

        TrainingDto result = trainingService.getTraining("10");

        assertEquals("10", result.getId());
        assertEquals("trainee-1", result.getTraineeId());
        assertEquals("trainer-1", result.getTrainerId());
        assertEquals("BOXING", result.getType());
    }


    @Test
    @DisplayName("Should throw GymGymNotFoundException when training ID not found")
    void shouldThrowIfTrainingNotFound() {
        when(trainingDao.findById("99")).thenReturn(null);
        assertThrows(GymNotFoundException.class, () -> trainingService.getTraining("99"));
    }

    @Test
    @DisplayName("Should return list of all trainings")
    void shouldReturnAllTrainings() {
        Training training = new Training("trainee-1", "trainer-1", "2025-01-01", TrainingType.YOGA, 30);
        training.setId("1");

        when(trainingDao.findAll()).thenReturn(List.of(training));
        when(userDao.findById("trainee-1")).thenReturn(new User("ali", "trainee", Collections.emptyList()));
        when(userDao.findById("trainer-1")).thenReturn(new User("veli", "trainer", Collections.emptyList()));

        List<TrainingDto> result = trainingService.getAllTrainings();

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("YOGA", result.get(0).getType());
    }

    @Test
    @DisplayName("Should update training and call save")
    void shouldCallUpdateTraining() {
        String trainingId = "1";
        CreateTrainingRequest request = new CreateTrainingRequest("trainee-new", "trainer-new", "2025-05-01", "CROSSFIT", 75);
        Training existing = new Training("trainee-old", "trainer-old", "2024-01-01", TrainingType.YOGA, 45);
        existing.setId(trainingId);

        User trainee = new User("ali", "trainee", Collections.emptyList());
        trainee.setId("trainee-new");

        User trainer = new User("veli", "trainer", Collections.emptyList());
        trainer.setId("trainer-new");

        when(trainingDao.findById(trainingId)).thenReturn(existing);
        when(trainingDao.save(existing)).thenReturn(existing);
        when(userDao.findById("trainee-new")).thenReturn(trainee);
        when(userDao.findById("trainer-new")).thenReturn(trainer);

        TrainingDto result = trainingService.updateTraining(trainingId, request);

        assertEquals("CROSSFIT", result.getType());
        assertEquals(75, result.getDurationMinutes());
        assertEquals("trainee-new", result.getTraineeId());

        verify(trainingDao).save(existing);
    }

    @Test
    @DisplayName("Should delete training when exists")
    void shouldDeleteTraining() {
        Training training = new Training("trainee", "trainer", "2025-01-01", TrainingType.YOGA, 50);
        training.setId("5");

        when(trainingDao.findById("5")).thenReturn(training);

        trainingService.deleteTraining("5");

        verify(trainingDao).delete("5");
    }

    @Test
    @DisplayName("Should throw GymGymNotFoundException when deleting non-existing training")
    void shouldThrowWhenDeletingNonExistingTraining() {
        when(trainingDao.findById("not-exist")).thenReturn(null);

        assertThrows(GymNotFoundException.class, () -> trainingService.deleteTraining("not-exist"));
    }
}
