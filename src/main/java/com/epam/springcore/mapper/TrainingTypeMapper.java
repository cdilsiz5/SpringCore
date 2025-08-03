package com.epam.springcore.mapper;

import com.epam.springcore.dto.TrainingTypeDto;
import com.epam.springcore.model.TrainingType;
import org.mapstruct.Mapper;


import java.util.List;
@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDto toTrainingTypeDto(TrainingType trainingType);

    List<TrainingTypeDto> toTrainingTypeDtoList(List<TrainingType> trainingType) ;


}
