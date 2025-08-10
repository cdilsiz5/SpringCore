package com.epam.gymcrm.mapper;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.model.TrainingType;
import org.mapstruct.Mapper;


import java.util.List;
@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingTypeDto toTrainingTypeDto(TrainingType trainingType);

    List<TrainingTypeDto> toTrainingTypeDtoList(List<TrainingType> trainingType) ;


}
