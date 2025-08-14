package com.epam.gymcrm.mapper.impl;

import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.dto.UserDto;
import com.epam.gymcrm.mapper.TraineeMapper;
import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.request.trainee.CreateTraineeRequest;
import com.epam.gymcrm.request.trainee.UpdateTraineeRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraineeMapperImpl implements TraineeMapper {

    @Override
    public TraineeDto toTraineeDto(Trainee trainee) {
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

        List<TrainerDto> trainerDtos = new ArrayList<>();
        if (trainee.getTrainers() != null) {
            for (Trainer trainer : trainee.getTrainers()) {
                trainerDtos.add(toTrainerDto(trainer));
            }
        }

        return TraineeDto.builder()
                .id(trainee.getId())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .user(userDto)
                .trainers(trainerDtos)
                .build();
    }

    private TrainerDto toTrainerDto(Trainer trainer) {
        if (trainer == null) return null;

        User user = trainer.getUser();
        UserDto userDto = null;

        if (user != null) {
            userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userActive(user.isUserActive())
                    .build();
        }

        return TrainerDto.builder()
                .id(trainer.getId())
                .specialization(trainer.getSpecialization() != null
                        ? trainer.getSpecialization().name()
                        : null)
                .user(userDto)
                .build();
    }

    @Override
    public List<TraineeDto> toTraineeDtoList(List<Trainee> trainees) {
        if (trainees == null) return new ArrayList<>();
        return trainees.stream()
                .map(this::toTraineeDto)
                .collect(Collectors.toList());
    }

@Override
    public Trainee createTrainee(CreateTraineeRequest request) {
        if (request == null) return null;

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userActive(true)

                .build();

        return Trainee.builder()
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .user(user)
                .build();
    }

    @Override
    public void updateTraineeRequest(UpdateTraineeRequest request, Trainee trainee) {
        if (request == null || trainee == null) return;

        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
    }
}
