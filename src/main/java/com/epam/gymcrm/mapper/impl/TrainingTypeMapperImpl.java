package com.epam.gymcrm.mapper.impl;

import com.epam.gymcrm.dto.TrainingTypeDto;
import com.epam.gymcrm.mapper.TrainingTypeMapper;
import com.epam.gymcrm.model.TrainingType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrainingTypeMapperImpl implements TrainingTypeMapper {

    @Override
    public TrainingTypeDto toTrainingTypeDto(TrainingType trainingType) {
        if (trainingType == null) return null;

        return TrainingTypeDto.builder()
                .id(trainingType.getId().intValue())
                .specialization(trainingType.getName())
                .build();
    }

    @Override
    public List<TrainingTypeDto> toTrainingTypeDtoList(List<TrainingType> trainingTypes) {
        if (trainingTypes == null) return null;

        List<TrainingTypeDto> dtoList = new ArrayList<>();
        for (TrainingType type : trainingTypes) {
            dtoList.add(toTrainingTypeDto(type));
        }
        return dtoList;
    }
}
