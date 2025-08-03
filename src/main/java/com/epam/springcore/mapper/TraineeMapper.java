package com.epam.springcore.mapper;
import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraineeMapper {

    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    TraineeDto toTraineeDto(Trainee trainee);

    List<TraineeDto> toTraineeDtoList(List<Trainee> trainees);

    Trainee createTrainee(CreateTraineeRequest request);

    void updateTraineeRequest(UpdateTraineeRequest request, @MappingTarget Trainee trainee);
}
