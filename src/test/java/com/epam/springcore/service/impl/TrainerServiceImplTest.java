package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.CreateTrainerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TrainerServiceImpl")
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create a new trainer and call save methods")
    void shouldCallCreateMethods() {
        CreateTrainerRequest request = new CreateTrainerRequest("ali", "yılmaz", TrainingType.CROSSFIT);
        User user = new User("ali", "yılmaz", Collections.emptyList());
        Trainer trainer = new Trainer(TrainingType.CROSSFIT, user.getId());

        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        doNothing().when(userDao).save(any(User.class));
        when(trainerDao.save(any(Trainer.class))).thenReturn(trainer);
        when(userDao.findById(user.getId())).thenReturn(user);

        trainerService.createTrainer(request);

        verify(userDao).save(any(User.class));
        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should return trainer DTO when ID is found")
    void shouldReturnTrainerById() {
        String trainerId = "1";
        Trainer trainer = new Trainer(TrainingType.YOGA, "5");
        User user = new User("mehmet", "arslan", Collections.emptyList());

        when(trainerDao.findById(trainerId)).thenReturn(trainer);
        when(userDao.findById("5")).thenReturn(user);

        TrainerDto result = trainerService.getTrainer(trainerId);

        assertNotNull(result);
        assertEquals("mehmet", result.getFirstName());
        assertEquals("YOGA", result.getSpecialization());
    }

    @Test
    @DisplayName("Should throw NotFoundException when trainer ID not found")
    void shouldThrowWhenTrainerNotFound() {
        when(trainerDao.findById("1")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> trainerService.getTrainer("1"));
    }

    @Test
    @DisplayName("Should return all trainers as DTO list")
    void shouldReturnAllTrainers() {
        Trainer trainer = new Trainer(TrainingType.STRENGTH, "10");
        User user = new User("veli", "kar", Collections.emptyList());

        when(trainerDao.findAll()).thenReturn(List.of(trainer));
        when(userDao.findById("10")).thenReturn(user);

        List<TrainerDto> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals("veli", result.get(0).getFirstName());
        assertEquals("STRENGTH", result.get(0).getSpecialization());
    }

    @Test
    @DisplayName("Should call update methods when updating a trainer")
    void shouldCallUpdateMethods() {
        String trainerId = "1";
        String userId = "99";
        CreateTrainerRequest request = new CreateTrainerRequest("ahmet", "kaya", TrainingType.YOGA);
        Trainer existingTrainer = new Trainer(TrainingType.CROSSFIT, userId);
        User existingUser = new User("old", "user", Collections.emptyList());
        existingUser.setId(userId);
        existingUser.setFirstName("ahmet");
        existingUser.setLastName("kaya");
        existingUser.setUsername("ahmet.kaya");

        when(trainerDao.findById(trainerId)).thenReturn(existingTrainer);
        when(userDao.findById(userId)).thenReturn(existingUser);
        doNothing().when(userDao).save(any(User.class));
        when(trainerDao.save(existingTrainer)).thenReturn(existingTrainer);
        when(userDao.findById(userId)).thenReturn(existingUser);

        trainerService.updateTrainer(trainerId, request);

        verify(userDao).save(any(User.class));
        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should delete trainer successfully")
    void shouldDeleteTrainer() {
        Trainer trainer = new Trainer(TrainingType.YOGA, "user-1");
        when(trainerDao.findById("1")).thenReturn(trainer);

        assertDoesNotThrow(() -> trainerService.deleteTrainer("1"));
        verify(trainerDao).delete("1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existing trainer")
    void shouldThrowWhenDeletingNonExistingTrainer() {
        when(trainerDao.findById("2")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> trainerService.deleteTrainer("2"));
    }
}
