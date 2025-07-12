package com.epam.springcore.mapper;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.request.CreateTrainerRequest;
import com.epam.springcore.request.CreateTrainingRequest;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface TrainerMapper {

    TrainerMapper TRAINER_MAPPER = Mappers.getMapper(TrainerMapper.class);

    TrainerDto toTrainerDto(Trainer trainer);

    List<TrainerDto> toTrainerDtoList(List<Trainer> trainers);

    Trainer toTrainer(CreateTrainerRequest request);

    void updateTrainer(CreateTrainerRequest request, @MappingTarget Trainer trainer);
}


