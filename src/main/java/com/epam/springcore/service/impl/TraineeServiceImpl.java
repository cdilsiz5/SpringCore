package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.repository.TraineeRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements ITraineeService {
    private final TraineeRepository traineeRepository;
    private final IUserService userService;

    @Override
    public TraineeDto createTrainee(CreateTraineeRequest request) {
        return null;
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        return null;
    }

    @Override
    public List<TraineeDto> getAllTrainees() {
        return List.of();
    }

    @Override
    public TraineeDto updateTrainee(String username, UpdateTraineeRequest request) {
        return null;
    }

    @Override
    public void deleteTrainee(String username) {

    }

    @Override
    public TraineeDto activateOrDeactivate(String username) {
        return null;
    }

    @Override
    public TraineeDto updateTrainerList(String username, UpdateTraineeTrainerListRequest request) {
        return null;
    }

    @Override
    public List<TraineeDto> getTrainingHistory(String username, String from, String to, String trainerName, String trainingType) {
        return List.of();
    }

    @Override
    public List<TraineeDto> getUnassignedTrainers(String username) {
        return List.of();
    }
}