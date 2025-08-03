package com.epam.springcore.mapper;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.model.Training;

import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;
@Mapper(componentModel = "spring")

public interface TrainingMapper {


    TrainingDto toTrainingDto(Training training);

    List<TrainingDto> toTrainingDtoList(List<Training> trainingType);

    Training createTraining(CreateTrainingRequest request);

    void updateTrainingRequest(UpdateTrainingRequest request, @MappingTarget Training training);

}
