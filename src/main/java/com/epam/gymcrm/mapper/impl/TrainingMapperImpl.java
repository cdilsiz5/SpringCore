package com.epam.gymcrm.mapper.impl;

import com.epam.gymcrm.dto.*;
import com.epam.gymcrm.mapper.TrainingMapper;
import com.epam.gymcrm.model.*;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingMapperImpl implements TrainingMapper {

    @Override
    public TrainingDto toTrainingDto(Training training) {
        if (training == null) return null;

        return TrainingDto.builder()
                .id(training.getId())
                .date(training.getDate())
                .durationMinutes(training.getDurationMinutes())
                .trainer(toTrainerDtoSummary(training.getTrainer()))
                .trainee(toTraineeDtoSummary(training.getTrainee()))
                .trainingType(toTypeDtoSummary(training.getTrainingType()).toString())
                .build();
    }

    @Override
    public List<TrainingDto> toTrainingDtoList(List<Training> trainings) {
        if (trainings == null) return new ArrayList<>();
        return trainings.stream()
                .map(this::toTrainingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTrainingRequest(UpdateTrainingRequest request, Training training) {
        if (request == null || training == null) return;

        if (request.getDate() != null && !request.getDate().isBlank()) {
            training.setDate(LocalDate.parse(request.getDate()));
        }

        if (request.getType() != null) {
            TrainingType trainingType = new TrainingType();
            trainingType.setId(request.getType());
            training.setTrainingType(trainingType);
        }

        if (request.getDurationMinutes() != null) {
            training.setDurationMinutes(request.getDurationMinutes());
        }
    }



    private UserDto toUserDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userActive(user.isUserActive())
                .build();
    }

    private TrainerDto toTrainerDtoSummary(Trainer trainer) {
        if (trainer == null) return null;

        return TrainerDto.builder()
                .id(trainer.getId())
                .specialization(trainer.getSpecialization() != null
                        ? trainer.getSpecialization().name()
                        : null)
                .user(toUserDto(trainer.getUser()))
                .build();
    }

    private TraineeDto toTraineeDtoSummary(Trainee trainee) {
        if (trainee == null) return null;

        return TraineeDto.builder()
                .id(trainee.getId())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .user(toUserDto(trainee.getUser()))
                .build();
    }

    private TrainingTypeDto toTypeDtoSummary(TrainingType trainingType) {
        if (trainingType == null) return null;

        return TrainingTypeDto.builder()
                .id(trainingType.getId().intValue())
                .specialization(trainingType.getName())
                .build();
    }
}
