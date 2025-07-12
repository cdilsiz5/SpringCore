package com.epam.springcore.mapper;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;
import com.epam.springcore.request.CreateTrainingRequest;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface TrainingMapper {

    TrainingMapper TRAINING_MAPPER = Mappers.getMapper(TrainingMapper.class);

    TrainingDto toTrainingDto(Training training);

    List<TrainingDto> toTrainingDtoList(List<Training> trainings);

    Training toTraining(CreateTrainingRequest request);

    void updateTraining(CreateTrainingRequest request, @MappingTarget Training training);
}


