package com.epam.gymcrm.mapper;

import com.epam.gymcrm.dto.*;
import com.epam.gymcrm.model.*;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TrainingMapper {

    @Mapping(target = "trainer", qualifiedByName = "trainerSummary")
    @Mapping(target = "trainee", qualifiedByName = "traineeSummary")
    @Mapping(target = "trainingType", qualifiedByName = "typeSummary")
    TrainingDto toTrainingDto(Training training);

    List<TrainingDto> toTrainingDtoList(List<Training> trainings);

    void updateTrainingRequest(UpdateTrainingRequest request, @MappingTarget Training training);


    @Named("userSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "firstName", source = "firstName"),
            @Mapping(target = "lastName", source = "lastName"),
            @Mapping(target = "userActive", source = "userActive")
    })
    UserDto toUserDto(User user);

    @Named("trainerSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "specialization", expression = "java(trainer.getSpecialization() != null ? trainer.getSpecialization().name() : null)"),
            @Mapping(target = "user", source = "user", qualifiedByName = "userSummary")
    })
    TrainerDto toTrainerDtoSummary(Trainer trainer);

    @Named("traineeSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "dateOfBirth", source = "dateOfBirth"),
            @Mapping(target = "user", source = "user", qualifiedByName = "userSummary")
    })
    TraineeDto toTraineeDtoSummary(Trainee trainee);

    @Named("typeSummary")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrainingTypeDto toTypeDtoSummary(TrainingType trainingType);
}
