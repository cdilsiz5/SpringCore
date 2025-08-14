package com.epam.gymcrm.mapper.impl;

import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.mapper.TrainerMapper;
import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerMapperImpl implements TrainerMapper {

    @Override
    public TrainerDto toTrainerDto(Trainer trainer) {
        if (trainer == null) return null;

        // User → UserDto
        UserDto userDto = null;
        User user = trainer.getUser();
        if (user != null) {
            userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userActive(user.isUserActive())
                    .build();
        }


        List<TraineeDto> traineeDtos = new ArrayList<>();
        if (trainer.getTrainees() != null) {
            for (Trainee trainee : trainer.getTrainees()) {
                traineeDtos.add(toTraineeDto(trainee));
            }
        }

        return TrainerDto.builder()
                .id(trainer.getId())
                .specialization(trainer.getSpecialization() != null
                        ? trainer.getSpecialization().name()
                        : null)
                .user(userDto)
                .trainees(traineeDtos)
                .build();
    }

    private TraineeDto toTraineeDto(Trainee trainee) {
        if (trainee == null) return null;

        UserDto userDto = null;
        User user = trainee.getUser();
        if (user != null) {
            userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userActive(user.isUserActive())
                    .build();
        }

        return TraineeDto.builder()
                .id(trainee.getId())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .user(userDto)
                .build();
    }


@Override
    public List<TrainerDto> toTrainerDtoList(List<Trainer> trainers) {
        if (trainers == null) return new ArrayList<>();
        return trainers.stream()
                .map(this::toTrainerDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTrainerRequest(UpdateTrainerRequest request, Trainer trainer) {
        if (request == null || trainer == null) return;
        trainer.setSpecialization(request.getSpecialization());
    }
}
