package com.epam.springcore.mapper;
import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface TraineeMapper {


    TraineeDto toTraineeDto(Trainee trainee);

    List<TraineeDto> toTraineeDtoList(List<Trainee> trainees);

    Trainee createTrainee(CreateTraineeRequest request);

    void updateTraineeRequest(UpdateTraineeRequest request, @MappingTarget Trainee trainee);

}
