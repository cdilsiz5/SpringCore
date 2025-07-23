package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.dto.TrainingTypeDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TrainingMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.TrainingType;
import com.epam.springcore.repository.TrainingRepository;
import com.epam.springcore.repository.TrainingTypeRepository;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for TrainingServiceImpl")
public class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create training and return DTO")
    void shouldCreateTrainingSuccessfully() {
        CreateTrainingRequest request = new CreateTrainingRequest();
        Training training = new Training();
        Training saved = new Training();
        TrainingDto dto = new TrainingDto();
        TrainingType trainingType = new TrainingType();

        when(trainingMapper.createTraining(request)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(saved);
        when(trainingMapper.toTrainingDto(saved)).thenReturn(dto);
        when(trainingTypeRepository.findById(request.getType())).thenReturn(Optional.of(trainingType));
        TrainingDto result = trainingService.createTraining(request);

        assertNotNull(result);
        verify(trainingRepository).save(training);
    }

    @Test
    @DisplayName("Should fetch training by ID")
    void shouldReturnTrainingById() {
        Long id = 1L;
        Training training = new Training();
        TrainingDto dto = new TrainingDto();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        when(trainingMapper.toTrainingDto(training)).thenReturn(dto);

        TrainingDto result = trainingService.getTraining(id);

        assertNotNull(result);
        verify(trainingRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw NotFoundException when training ID not found")
    void shouldThrowIfTrainingNotFoundById() {
        Long id = 100L;
        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.getTraining(id));
    }

    @Test
    @DisplayName("Should fetch all trainings as DTO list")
    void shouldReturnAllTrainings() {
        List<Training> trainings = List.of(new Training());
        List<TrainingDto> dtoList = List.of(new TrainingDto());

        when(trainingRepository.findAll()).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(dtoList);

        List<TrainingDto> result = trainingService.getAllTrainings();

        assertEquals(1, result.size());
        verify(trainingRepository).findAll();
    }

    @Test
    @DisplayName("Should update training and return DTO")
    void shouldUpdateTrainingSuccessfully() {
        Long id = 10L;
        UpdateTrainingRequest request = new UpdateTrainingRequest();
        Training training = new Training();
        Training saved = new Training();
        TrainingDto dto = new TrainingDto();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        doNothing().when(trainingMapper).updateTrainingRequest(request, training);
        when(trainingRepository.save(training)).thenReturn(saved);
        when(trainingMapper.toTrainingDto(saved)).thenReturn(dto);

        TrainingDto result = trainingService.updateTraining(id, request);

        assertNotNull(result);
        verify(trainingRepository).save(training);
    }

    @Test
    @DisplayName("Should delete training when exists")
    void shouldDeleteTrainingById() {
        Long id = 5L;
        Training training = new Training();

        when(trainingRepository.findById(id)).thenReturn(Optional.of(training));
        doNothing().when(trainingRepository).delete(training);

        assertDoesNotThrow(() -> trainingService.deleteTraining(id));
        verify(trainingRepository).delete(training);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent training")
    void shouldThrowWhenDeletingNonExistentTraining() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.deleteTraining(99L));
    }

    @Test
    @DisplayName("Should return trainings by trainer")
    void shouldReturnTrainingsByTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        List<Training> trainings = List.of(new Training());
        List<TrainingDto> dtoList = List.of(new TrainingDto());

        when(trainingRepository.findAllByTrainer(trainer)).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(dtoList);

        List<TrainingDto> result = trainingService.findAllByTrainer(trainer);

        assertEquals(1, result.size());
        verify(trainingRepository).findAllByTrainer(trainer);
    }

    @Test
    @DisplayName("Should return trainings by trainee")
    void shouldReturnTrainingsByTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(2L);
        List<Training> trainings = List.of(new Training());
        List<TrainingDto> dtoList = List.of(new TrainingDto());

        when(trainingRepository.findAllByTrainee(trainee)).thenReturn(trainings);
        when(trainingMapper.toTrainingDtoList(trainings)).thenReturn(dtoList);

        List<TrainingDto> result = trainingService.findAllByTrainee(trainee);

        assertEquals(1, result.size());
        verify(trainingRepository).findAllByTrainee(trainee);
    }
}
