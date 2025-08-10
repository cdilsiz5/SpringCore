package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.model.Training;

import com.epam.gymcrm.request.training.CreateTrainingRequest;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;
import com.epam.gymcrm.response.TrainingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;
@Mapper(componentModel = "spring")

public interface TrainingMapper {


    TrainingDto toTrainingDto(Training training);

    List<TrainingDto> toTrainingDtoList(List<Training> trainingType);

    Training createTraining(CreateTrainingRequest request);

    void updateTrainingRequest(UpdateTrainingRequest request, @MappingTarget Training training);


    @Mapping(source = "trainee.id",                  target = "traineeId")
    @Mapping(source = "trainee.user.firstName",      target = "traineeFirstName")
    @Mapping(source = "trainee.user.lastName",       target = "traineeLastName")
    @Mapping(source = "trainer.id",                  target = "trainerId")
    @Mapping(source = "trainer.user.firstName",      target = "trainerFirstName")
    @Mapping(source = "trainer.user.lastName",       target = "trainerLastName")
    @Mapping(source = "trainingType.id",             target = "trainingTypeId")
    @Mapping(source = "trainingType.name",           target = "trainingTypeName")
    TrainingResponse toResponse(Training training);

}
