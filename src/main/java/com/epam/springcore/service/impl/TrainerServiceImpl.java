package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TrainerMapper;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainerRepository;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class TrainerServiceImpl implements ITrainerService {

    private final TrainerRepository trainerRepository;
    private final ITrainingService trainingService;
    private final TrainerMapper trainerMapper;

    @Override
    public TrainerDto createTrainerEntity(User user, Specialization specialty) {
        log.info("Creating Trainer entity for user ID: {}, specialization: {}", user.getId(), specialty);
        Trainer trainer = Trainer.builder()
                .specialization(specialty)
                .user(user)
                .build();
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Trainer saved with ID: {}", savedTrainer.getId());
        return trainerMapper.toTrainerDto(savedTrainer);
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        log.info("Fetching Trainer by username: {}", username);
        return trainerMapper.toTrainerDto(getTrainerEntityByUsername(username));
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        log.info("Fetching all trainers");
        List<TrainerDto> trainers = trainerMapper.toTrainerDtoList(trainerRepository.findAll());
        log.debug("Total trainers fetched: {}", trainers.size());
        return trainers;
    }

    @Override
    public TrainerDto updateTrainer(String username, UpdateTrainerRequest request) {
        log.info("Updating Trainer with username: {}", username);
        Trainer trainer = getTrainerEntityByUsername(username);
        trainerMapper.updateTrainerRequest(request, trainer);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Trainer updated. ID: {}", updatedTrainer.getId());
        return trainerMapper.toTrainerDto(updatedTrainer);
    }

    @Override
    public void deleteTrainer(String username) {
        log.info("Deleting Trainer with username: {}", username);
        Trainer trainer = getTrainerEntityByUsername(username);
        trainerRepository.delete(trainer);
        log.info("Trainer deleted. ID: {}", trainer.getId());
    }

    @Override
    public List<TrainingDto> getTrainingHistory(String username, LocalDate from, LocalDate to, String traineeName, String traineeLastName) {
        log.info("Fetching training history for trainer: {} | From: {} To: {} | Filter: {} {}",
                username, from, to, traineeName, traineeLastName);
        Trainer trainer = getTrainerEntityByUsername(username);
        List<TrainingDto> trainings = trainingService.findAllByTrainer(trainer);
        List<TrainingDto> filtered = trainings.stream()
                .filter(t -> from == null || !t.getDate().isBefore(from))
                .filter(t -> to == null || !t.getDate().isAfter(to))
                .filter(t -> {
                    if (traineeName == null && traineeLastName == null) return true;
                    String first = t.getTrainee().getUser().getFirstName().toLowerCase();
                    String last = t.getTrainee().getUser().getLastName().toLowerCase();
                    boolean firstMatch = traineeName == null || first.contains(traineeName.toLowerCase());
                    boolean lastMatch = traineeLastName == null || last.contains(traineeLastName.toLowerCase());
                    return firstMatch && lastMatch;
                })
                .collect(Collectors.toList());
        log.debug("Training history filtered result count: {}", filtered.size());
        return filtered;
    }

    @Override
    public Trainer getTrainerById(Long trainerId) {
        log.info("Fetching trainer by ID: {}", trainerId);
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> {
                    log.warn("Trainer not found with ID: {}", trainerId);
                    return new NotFoundException("Trainer with id " + trainerId + " not found");
                });
    }

    private Trainer getTrainerEntityByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found with username: {}", username);
                    return new NotFoundException("Trainer not found: " + username);
                });
    }
}
