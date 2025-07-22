package com.epam.springcore.mapper;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.model.Trainer;

import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;
@Mapper(componentModel = "spring")

public interface TrainerMapper {

  TrainerDto toTrainerDto(Trainer trainer);

    List<TrainerDto> toTrainerDtoList(List<Trainer> trainers);

  Trainer createTrainer(CreateTrainerRequest request);

    void updateTrainerRequest(UpdateTrainerRequest request, @MappingTarget Trainer trainer);

}
