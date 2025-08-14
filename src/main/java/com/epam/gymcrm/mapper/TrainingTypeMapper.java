package com.epam.gymcrm.mapper;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.model.TrainingType;


import java.util.List;
public interface TrainingTypeMapper {

    TrainingTypeDto toTrainingTypeDto(TrainingType trainingType);

    List<TrainingTypeDto> toTrainingTypeDtoList(List<TrainingType> trainingType) ;


}
