package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TrainerServiceImpl implements ITrainerService {

    @Override
    public TrainerDto createTrainer(CreateTrainerRequest request) {
        return null;
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        return null;
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        return List.of();
    }

    @Override
    public TrainerDto updateTrainer(String username, UpdateTrainerRequest request) {
        return null;
    }

    @Override
    public void deleteTrainer(String username) {

    }

    @Override
    public TrainerDto activateOrDeactivate(String username) {
        return null;
    }

    @Override
    public List<TrainerDto> getTrainingHistory(String username, String from, String to, String traineeName) {
        return List.of();
    }
}