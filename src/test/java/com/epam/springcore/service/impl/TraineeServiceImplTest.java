package com.epam.springcore.service.impl;

import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TraineeServiceImpl")
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeDao;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should call create methods when creating a trainee")
    void shouldCallCreateMethods() {
        CreateTraineeRequest request = new CreateTraineeRequest("ali", "yılmaz", LocalDate.of(2000, 1, 1), "istanbul");
        User user = new User("ali", "yılmaz", Collections.emptyList());
        Trainee trainee = new Trainee(request.getDateOfBirth(), request.getAddress(), user.getId());

        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        doNothing().when(userRepository).save(any(User.class));
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);
        when(userRepository.findById(user.getId())).thenReturn(user);

        traineeService.createTrainee(request);

        verify(userRepository).save(any(User.class));
        verify(traineeDao).save(any(Trainee.class));
    }



    @Test
    @DisplayName("Should return trainee DTO when ID is found")
    void getTrainee_shouldReturnDto() {
        String traineeId = "1";
        Trainee trainee = new Trainee(LocalDate.of(2000, 1, 1), "istanbul", "5");
        User user = new User("ali", "yılmaz", Collections.emptyList());

        when(traineeDao.findById(traineeId)).thenReturn(trainee);
        when(userRepository.findById("5")).thenReturn(user);

        TraineeDto result = traineeService.getTrainee(traineeId);

        assertNotNull(result);
        assertEquals("ali", result.getFirstName());
        assertEquals("istanbul", result.getAddress());
    }

    @Test
    @DisplayName("Should throw NotFoundException when trainee ID is not found")
    void getTrainee_shouldThrowNotFoundException() {
        when(traineeDao.findById("1")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> traineeService.getTrainee("1"));
    }

    @Test
    @DisplayName("Should return all trainees as DTO list")
    void getAllTrainees_shouldReturnList() {
        Trainee trainee = new Trainee(LocalDate.of(2000, 1, 1), "ankara", "10");
        User user = new User("veli", "demir", Collections.emptyList());

        when(traineeDao.findAll()).thenReturn(List.of(trainee));
        when(userRepository.findById("10")).thenReturn(user);

        List<TraineeDto> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals("veli", result.get(0).getFirstName());
        assertEquals("ankara", result.get(0).getAddress());
    }

    @Test
    @DisplayName("Should call update methods when updating a trainee")
    void shouldCallUpdateMethods() {
        String traineeId = "1";
        CreateTraineeRequest request = new CreateTraineeRequest("ahmet", "kaya", LocalDate.of(1999, 1, 1), "izmir");

        Trainee existingTrainee = new Trainee(LocalDate.of(2000, 1, 1), "eski", "20");
        User existingUser = new User("old", "user", Collections.emptyList());
        existingUser.setId("20");

        when(traineeDao.findById(traineeId)).thenReturn(existingTrainee);
        when(userRepository.findById("20")).thenReturn(existingUser);
        doNothing().when(userRepository).save(any(User.class));
        when(traineeDao.save(any(Trainee.class))).thenReturn(existingTrainee);

        traineeService.updateTrainee(traineeId, request);

        verify(userRepository).save(any(User.class));
        verify(traineeDao).save(any(Trainee.class));
    }




    @Test
    @DisplayName("Should delete trainee successfully")
    void deleteTrainee_shouldDelete() {
        Trainee trainee = new Trainee(LocalDate.now(), "ankara", "id-1");
        when(traineeDao.findById("1")).thenReturn(trainee);

        assertDoesNotThrow(() -> traineeService.deleteTrainee("1"));
        verify(traineeDao, times(1)).delete("1");
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existing trainee")
    void deleteTrainee_shouldThrowWhenNotFound() {
        when(traineeDao.findById("2")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee("2"));
    }
}
