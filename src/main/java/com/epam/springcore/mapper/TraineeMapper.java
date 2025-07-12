package com.epam.springcore.mapper;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.request.CreateTrainingRequest;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

public interface TraineeMapper {

    TraineeMapper TRAINEE_MAPPER = Mappers.getMapper(TraineeMapper.class);

    TraineeDto toTraineDto(Trainee trainee);

    List<TraineeDto> toTraineeList(List<Trainee> trainees);

    Trainee toTrainee(CreateTraineeRequest request);

    void updateTrainee(CreateTraineeRequest request, @MappingTarget Trainee trainee);
}
