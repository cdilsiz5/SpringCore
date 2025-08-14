package com.epam.gymcrm.mapper;

import com.epam.gymcrm.dto.TrainingDto;
import com.epam.gymcrm.model.Training;
import com.epam.gymcrm.request.training.UpdateTrainingRequest;

import java.util.List;

public interface TrainingMapper {

    TrainingDto toTrainingDto(Training training);

    List<TrainingDto> toTrainingDtoList(List<Training> trainings);

    void updateTrainingRequest(UpdateTrainingRequest request, Training training);
}
