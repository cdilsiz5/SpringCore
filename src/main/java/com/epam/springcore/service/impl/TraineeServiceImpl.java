package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.TraineeMapper;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.ITrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements ITraineeService {
    private final TraineeRepository traineeRepository;
    private final ITrainerService trainerService;
    private final ITrainingService trainingService;
    private final TraineeMapper traineeMapper;

    @Override
    public TraineeDto createTraineeEntity(User user, LocalDate dob, String address) {
        log.info("Creating trainee entity with dob: {}, address: {}", dob, address);
        Trainee trainee=Trainee.builder()
                .user(user)
                .dateOfBirth(dob)
                .address(address)
                .build();
        Trainee savedTrainee=traineeRepository.save(trainee);
        log.info("Trainee entity created: {}", savedTrainee);
        return traineeMapper.toTraineeDto(savedTrainee);
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        log.info("Fetching trainee by username: {}", username);
        Trainee trainee = getTraineeEntityByUsername(username);
        return traineeMapper.toTraineeDto(trainee);
    }


    @Override
    public List<TraineeDto> getAllTrainees() {
        log.info("Fetching all trainees");
        List<TraineeDto> list=traineeMapper.toTraineeDtoList(traineeRepository.findAll());
        log.debug("Total trainees fetched: {}", list.size());
        return list;
    }

    @Override
    public TraineeDto updateTrainee(String username, UpdateTraineeRequest request) {
        log.info("Updating Trainer with username: {}", username);
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeMapper.updateTraineeRequest(request, trainee);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee updated. ID: {}", updatedTrainee.getId());
        return traineeMapper.toTraineeDto(updatedTrainee);

    }

    @Override
    public void deleteTrainee(String username) {
        Trainee trainee = getTraineeEntityByUsername(username);
        traineeRepository.delete(trainee);
    }

    @Override
    @Transactional
    public TraineeDto updateTrainerList(String username, UpdateTraineeTrainerListRequest request) {
        log.info("Updating trainer list for trainee: {}", username);

        Trainee trainee = getTraineeEntityByUsername(username);

        Set<Trainer> trainers = request.getTrainerIds().stream()
                .map(trainerId -> {
                    Trainer trainer = trainerService.getTrainerById(trainerId);
                    return  trainer;
                })
                .collect(Collectors.toSet());
        trainee.setTrainers(trainers);

        Trainee updatedTrainee = traineeRepository.save(trainee);

        log.info("Trainer list updated for trainee '{}', assigned trainer count: {}", username, trainers.size());

        return traineeMapper.toTraineeDto(updatedTrainee);
    }


    @Override
    public List<TrainingDto> getTrainingHistory(String username, String from, String to, String trainerName, String trainerLastname, String trainingType) {
        log.info("Fetching training history for trainee: {} | From: {} To: {} | Filter: {} {} {}",
                username, from, to, trainerName, trainerLastname, trainingType);

        Trainee trainee = getTraineeEntityByUsername(username);
        List<TrainingDto> trainings = trainingService.findAllByTrainee(trainee);

        LocalDate fromDate = from != null ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null ? LocalDate.parse(to) : null;

        List<TrainingDto> filtered = trainings.stream()
                .filter(t -> fromDate == null || !t.getDate().isBefore(fromDate))
                .filter(t -> toDate == null || !t.getDate().isAfter(toDate))
                .filter(t -> {
                    boolean nameMatch = trainerName == null || t.getTrainer().getUser().getFirstName().toLowerCase().contains(trainerName.toLowerCase());
                    boolean lastMatch = trainerLastname == null || t.getTrainer().getUser().getLastName().toLowerCase().contains(trainerLastname.toLowerCase());
                    boolean typeMatch = trainingType == null || t.getTrainingType().toString().equalsIgnoreCase(trainingType);
                    return nameMatch && lastMatch && typeMatch;
                })
                .collect(Collectors.toList());

        log.debug("Filtered training history count: {}", filtered.size());
        return filtered;
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String username) {
        log.info("Fetching unassigned trainers for trainee: {}", username);

        Trainee trainee = getTraineeEntityByUsername(username);

        Set<Trainer> assignedTrainers = trainee.getTrainers();

        List<TrainerDto> allTrainers = trainerService.getAllTrainers();

        List<TrainerDto> unassignedTrainers = allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .toList();
        log.debug("Unassigned trainers found for trainee '{}': {}", username, unassignedTrainers.size());
        return unassignedTrainers;
    }

    private Trainee getTraineeEntityByUsername(String username) {
        Trainee trainee=traineeRepository.findByUserUsername(username).orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
        return trainee;
    }
}